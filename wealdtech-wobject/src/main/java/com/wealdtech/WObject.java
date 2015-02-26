/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import com.wealdtech.jackson.WealdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * The Weald Technology object
 * A generic immutable object which allows for arbitrary storage of data, serialization and deserialization through
 * Jackson, and object validation
 */
public class WObject<T extends WObject> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  // Helper predicate to strip out the internal fields (which start with "_")
  private static final Predicate<String> FILTER_OUT_INTERNAL_PREDICATE = new Predicate<String>(){
    @Override
    public boolean apply(@Nullable final String input)
    {
      return input != null && !input.startsWith("_");}
  };

  // Mapper used to read and write data
  static final SimpleModule module = new SimpleModule("orderedmaps", Version.unknownVersion()).addAbstractTypeMapping(Map.class,
                                                                                                                      TreeMap.class);
  private static final ObjectMapper MAPPER;
  static
  {
    MAPPER = WealdMapper.getMapper()
                        .copy()
                        .registerModule(module)
                        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                        .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
    ;
  }

  /**
   * Obtain the Object Mapper used in WObject Jackson operations.
   * This is actually a copy of the object mapper, so changes made it it will not affect WObject serialization
   * @return a copy of the Object Mapper used in WObject Jackson operations
   */
  public static ObjectMapper getObjectMapper() { return MAPPER.copy(); }

  // Internal fields
  @JsonIgnore
  protected static final String ID = "_id";
  @JsonIgnore
  private static final TypeReference<WID<?>> ID_TYPE_REF = new TypeReference<WID<?>>(){};

  // We store data as a sorted map to aid legibility
  @JsonIgnore
  protected final SortedMap<String, Object> data;

  @JsonAnyGetter
  private Map<String, Object> any() {
    return data;
  }

  @JsonCreator
  public WObject(final Map<String, Object> data)
  {
    this.data = order(preCreate(Maps.filterValues(data, Predicates.notNull())));
    validate();
  }

  // Recursively order data
  private SortedMap<String, Object> order(final Map<String, Object> map)
  {
    final TreeMap<String, Object> result = Maps.newTreeMap();
    for (final Map.Entry<String, Object> entry : map.entrySet())
    {
      if (entry.getValue() instanceof Map)
      {
        result.put(entry.getKey(), order((Map<String, Object>)entry.getValue()));
      }
      else if (entry.getValue() instanceof List)
      {
        result.put(entry.getKey(), order((List)entry.getValue()));
      }
      else
      {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  // We aren't re-ordering the list but if it has objects inside it we need to order them
  private List<Object> order(final List<Object> list)
  {
    final List<Object> result = Lists.newArrayList();
    for (final Object value : list)
    {
      if (value instanceof Map)
      {
        result.add(order((Map<String, Object>)value));
      }
      else if (value instanceof List)
      {
        result.add(order((List)value));
      }
      else
      {
        result.add(value);
      }
    }
    return result;
  }

  // Recursively deep-immutify data
  private ImmutableSortedMap<String, Object> immutify(final Map<String, Object> map)
  {
    final ImmutableSortedMap.Builder<String, Object> result = ImmutableSortedMap.naturalOrder();
    for (final Map.Entry<String, Object> entry : map.entrySet())
    {
      if (entry.getValue() instanceof Map)
      {
        result.put(entry.getKey(), immutify((Map<String, Object>)entry.getValue()));
      }
      else if (entry.getValue() instanceof List)
      {
        result.put(entry.getKey(), immutify((List)entry.getValue()));
      }
      else
      {
        result.put(entry);
      }
    }
    return result.build();
  }

  private ImmutableList<Object> immutify(final List<Object> list)
  {
    final ImmutableList.Builder<Object> result = ImmutableList.builder();
    for (final Object value : list)
    {
      if (value instanceof Map)
      {
        result.add(immutify((Map<String, Object>)value));
      }
      else if (value instanceof List)
      {
        result.add(immutify((List)value));
      }
      else
      {
        result.add(value);
      }
    }
    return result.build();
  }

  /**
   * Carry out any operations required to manage the object prior to creation.  For example, this could add a
   * timestamp or a version number.
   *
   * @param data the data supplied
   * @return the data to be used in creation of the object
   */
  protected Map<String, Object> preCreate(final Map<String, Object> data) { return data; }

  /**
   * Validate the data in the object to ensure that it conforms to whatever requirements it has.
   * This should throw a DataError if validation is not successful
   */
  protected void validate() {}


  /**
   * Obtain the raw data for this object.  Note that this does not provide the internal data.  Also note that there are no
   * guarantees about the types of the objects returned as values in the map; specifically, it is possible that the contents of
   * the map will change from one call to the next.
   * @see #getAllData
   * @return a map of keyed data objects
   */
  @JsonIgnore
  public ImmutableMap<String, Object> getData() { return externalData(); }

  /**
   * Obtain the raw data for this object.  Note that this does provide the internal data.  Also note that there are no
   * guarantees about the types of the objects returned as values in the map; specifically, it is possible that the values will
   * change between invocations (although the values returned from a single invocation will not).
   * @see #getData
   * @return a map of keyed data objects
   */
  @JsonIgnore
  public ImmutableMap<String, Object> getAllData() { return ImmutableMap.copyOf(data); }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  @Nullable
  public WID<T> getId() { return (WID<T>)get(ID, ID_TYPE_REF).orNull();}


  private static final TypeReference<WObject> GENERIC_TYPE_REF = new TypeReference<WObject>(){};
  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <U extends WObject> Optional<U> get(final String key)
  {
    return (Optional<U>)get(key, GENERIC_TYPE_REF);
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <U> Optional<U> get(final String key, final TypeReference<U> typeRef)
  {
    LOG.trace("Attempting to fetch {} as {}", key, typeRef.getType());
    final Object val = data.get(key);
    if (val == null)
    {
      return Optional.absent();
    }
    return getValue(key, val, typeRef);
  }

  protected <U> Optional<U> getValue(final String key, final Object val, final TypeReference<U> typeRef)
  {
    // Obtain the type we are after through reflection to find out if it is a collection
//    final Type type = typeRef.getType() instanceof ParameterizedType ? ((ParameterizedType)typeRef.getType()).getRawType() : typeRef.getType();
//    final String typeName = type.toString().replace("class ", "").replace("interface ", "");
//    boolean isCollection;
//    try
//    {
//      isCollection = Collection.class.isAssignableFrom(Class.forName(typeName));
//    }
//    catch (final ClassNotFoundException cnfe)
//    {
//      isCollection = false;
//    }

    final Class requiredClass = (Class)(typeRef.getType() instanceof ParameterizedType ? ((ParameterizedType)typeRef.getType()).getRawType() : typeRef.getType());
    final boolean isCollection = Collection.class.isAssignableFrom(requiredClass);
    if (val == null)
    {
      return Optional.absent();
    }

//    if (Objects.equal(typeName, val.getClass().getCanonicalName()))
    if (Objects.equal(requiredClass, val.getClass()))
    {
      return Optional.of((U)val);
    }
    final String valStr = stringify(val, isCollection);
    try
    {
      final U result = MAPPER.readValue(valStr, typeRef);
      this.data.put(key, result);
      return Optional.of(result);
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return Optional.absent();
    }
  }

  @JsonIgnore
  public <U> Optional<U> get(final String key, final Class<U> klazz)
  {
    LOG.trace("Attempting to fetch {} as {}", key, klazz.getSimpleName());
    final Object val = data.get(key);
    if (val == null)
    {
      return Optional.absent();
    }

    return getValue(key, val, klazz);
  }

  protected <U> Optional<U> getValue(final String key, final Object val, final Class<U> klazz)
  {
    if (val == null)
    {
      return Optional.absent();
    }

    if (Objects.equal(val.getClass(), klazz))
    {
      return Optional.of((U)val);
    }

    final String valStr = stringify(val, Collection.class.isAssignableFrom(klazz));
    try
    {
      final U result = MAPPER.readValue(valStr, klazz);
      data.put(key, result);
      return Optional.of(result);
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return Optional.absent();
    }
  }

  private String stringify(final Object val, final boolean isCollection)
  {
    String valStr;
    if (val instanceof String)
    {
      valStr = (String)val;
    }
    else
    {
      try
      {
        valStr = MAPPER.writeValueAsString(val);
      }
      catch (final IOException ioe)
      {
        LOG.error("Failed to encode value: ", ioe);
        return null;
      }
    }

    if (val instanceof Enum<?>)
    {
      return valStr;
    }

    // TODO need to escape " in the string (but only if in the string - already done?)
    if (!isCollection && !valStr.startsWith("{") && !valStr.startsWith("\""))
    {
      valStr = "\"" + valStr + "\"";
    }
    return valStr;
  }

  /**
   * Overlay another WObject on top of this one, updating where required
   *
   * @param overlay another WObject
   *
   * @return the combined WObject
   */
  public WObject<T> overlay(@Nullable final WObject<T> overlay)
  {
    if (overlay == null)
    {
      return this;
    }
    final Map<String, Object> data = Maps.newHashMap();
    data.putAll(data);
    data.putAll(overlay.data);

    return new WObject<>(data);
  }

  public boolean exists(final String key)
  {
    return data.containsKey(key);
  }

  /**
   * State if this object is empty or not.  Internal fields are not taken in to account when deciding this.
   * @return {@code true} if this object does not contain any useful data; otherwise {@code false}.
   */
  @JsonIgnore
  public boolean isEmpty()
  {
    return externalData().isEmpty();
  }

  @Override
  public String toString()
  {
    try
    {
      return MAPPER.writeValueAsString(this);
    }
    catch (final JsonProcessingException e)
    {
      System.err.println("Failed to generate string");
      return "{\"error\":\"toString() failed: " + e.getMessage() + "\"}";
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object that)
  {
    return that instanceof WObject && this.compareTo((T) that) == 0;
  }

  private ImmutableMap<String, Object> externalData()
  {
    return ImmutableMap.copyOf(Maps.filterKeys(this.data, FILTER_OUT_INTERNAL_PREDICATE));
  }

  @JsonIgnore
  private volatile int hashCode;

  // Lazy evaluation of the hash code
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      try
      {
        result = Objects.hashCode(MAPPER.writeValueAsString(externalData()));
      } catch (final IOException ioe)
      {
        LOG.error("Failed to generate external representation of object {}", data, ioe);
        throw new ServerError("Failed to generate external representation of object");
      }
      hashCode = result;
    }

    return result;
  }

  public int compareTo(@Nonnull T that)
  {
    // We cannot compare the objects directly because a native object might contain, for example,
    // a datetime whereas the deserialized object will contain a serialized String of that datetime.
    // The safest way to compare is to turn them both in to simple strings and compare them, although
    // this might not be cheap

    // We also want to remove any internal fields, as they don't count when carrying out comparisons
    try
    {
      return ComparisonChain.start()
                            .compare(MAPPER.writeValueAsString(this.getData()),
                                     MAPPER.writeValueAsString(that.getData()))
                            .result();
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to generate external representation of object {}", data, ioe);
      throw new ServerError("Failed to generate external representation of object");
    }
  }

  public T normalise(final Class<T> klazz)
  {
    return MAPPER.convertValue(this, klazz);
  }

  public T normalise(final TypeReference<T> typeReference)
  {
    return MAPPER.convertValue(this, typeReference);
  }

  public static class Builder<T extends WObject<T>, P extends Builder<T, P>>
  {
    protected Map<String, Object> data;

    public Builder(@Nullable final T prior)
    {
      data = Maps.newHashMap();
      if (prior != null)
      {
        data.putAll(prior.data);
      }
    }

    public Builder()
    {
      data = Maps.newHashMap();
    }

    public P data(final Map<String, ? extends Object> data)
    {
      this.data.putAll(data);
      return self();
    }

    public P id(final WID<? extends T> id)
    {
      this.data.put(ID, id);
      return self();
    }

    public P data(final String name, @Nullable final Object value)
    {
      this.data.put(name, value);
      return self();
    }

    @SuppressWarnings("unchecked")
    protected P self()
    {
      return (P)this;
    }

    public WObject<T> build()
    {
      return new WObject<>(data);
    }
  }
}
