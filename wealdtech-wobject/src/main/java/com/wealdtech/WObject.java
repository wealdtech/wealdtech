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
 * Jackson, and object validation.
 * A WObject contains three different types of data: external, internal and scratch  External data is the meat of the data, defining
 * the information held within the WObject.  External data is used for comparisons.  Internal data is metadata, such as ID, version
 * or last modified date.  Internal data is stored with external data when serialising the WObject, but is not used when comparing
 * objects for equality.  Scratch data is neither persisted nor used for comparison purposes, and is used to augment existing
 * objects with additional information rather than use secondary data structures
 */
public class WObject<T extends WObject> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  // Key for scratch data
  private static final String SCRATCH = "__scratch";

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

  /** A simple predicate to find scratch entries */
  private static final Predicate<String> SCRATCH_PREDICATE = new Predicate<String>(){
    @Override
    public boolean apply(@Nullable final String input)
    {
      return input != null && input.startsWith("__");
    }
  };

  /** A simple predicate to find not scratch entries */
  private static final Predicate<String> NOT_SCRATCH_PREDICATE = new Predicate<String>(){
    @Override
    public boolean apply(@Nullable final String input)
    {
      return input != null && !input.startsWith("__");
    }
  };

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

  // Scratch data is transient so held separately
  @JsonIgnore
  protected final Map<String, Object> scratchData;

  @JsonAnyGetter
  private Map<String, Object> any() {
    return data;
  }

  @JsonCreator
  public WObject(final Map<String, Object> data)
  {
    final Map<String, Object> preCreatedData = preCreate(Maps.filterValues(data, Predicates.notNull()));
    this.scratchData = Maps.filterKeys(preCreatedData, SCRATCH_PREDICATE);
    this.data = order(Maps.filterKeys(preCreatedData, NOT_SCRATCH_PREDICATE));
    validate();
  }

  // Recursively order data
  private SortedMap<String, Object> order(final Map<String, Object> map)
  {
    final TreeMap<String, Object> result = Maps.newTreeMap();
    for (final Map.Entry<String, Object> entry : map.entrySet())
    {
      // Don't need to handle WObjects here as they will already be ordered
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
      // Don't need to handle WObjects here as they will already be ordered
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

  // Recursively strip internal data
  private ImmutableSortedMap<String, Object> strip(final Map<String, Object> map)
  {
    final ImmutableSortedMap.Builder<String, Object> resultB = ImmutableSortedMap.naturalOrder();
    for (final Map.Entry<String, Object> entry : map.entrySet())
    {
      // The check for the key being a string is not redundant.  A map inside a WObject could have anything as its key, even though
      // our contract says it should be a string.  This is because someone could pass a value which is a map with a different class
      // for the key, for example WObject.builder().data("bad", ImmutableMap.<Integer, Integer>of(1, 1).build()
      // This is allowable so we need to handle it here
      if (!(entry.getKey() instanceof String) || !entry.getKey().startsWith("_"))
      {
        if (entry.getValue() instanceof WObject<?>)
        {
          resultB.put(entry.getKey(), new WObject(((WObject<?>)entry.getValue()).getData()));
        }
        else if (entry.getValue() instanceof Map)
        {
          resultB.put(entry.getKey(), strip((Map<String, Object>)entry.getValue()));
        }
        else if (entry.getValue() instanceof List)
        {
          resultB.put(entry.getKey(), strip((List)entry.getValue()));
        }
        else if (entry.getValue() instanceof Set)
        {
          resultB.put(entry.getKey(), strip((Set)entry.getValue()));
        }
        else if (entry.getValue() instanceof Collection)
        {
          resultB.put(entry.getKey(), strip((Collection)entry.getValue()));
        }
        else
        {
          resultB.put(entry.getKey(), entry.getValue());
        }
      }
    }
    return resultB.build();
  }

  private ImmutableList<Object> strip(final Collection<Object> collection)
  {
    final ImmutableList.Builder<Object> resultB = ImmutableList.builder();
    for (final Object value : collection)
    {
      if (value instanceof WObject<?>)
      {
        resultB.add(new WObject(((WObject<?>)value).getData()));
      }
      else if (value instanceof Map)
      {
        resultB.add(strip((Map<String, Object>)value));
      }
      else if (value instanceof List)
      {
        resultB.add(strip((List)value));
      }
      else if (value instanceof Set)
      {
        resultB.add(strip((Set)value));
      }
      else if (value instanceof Collection)
      {
        resultB.add(strip((Collection)value));
      }
      else
      {
        resultB.add(value);
      }
    }
    return resultB.build();
  }
  private ImmutableList<Object> strip(final List<Object> list)
  {
    final ImmutableList.Builder<Object> resultB = ImmutableList.builder();
    for (final Object value : list)
    {
      if (value instanceof WObject<?>)
      {
        resultB.add(new WObject(((WObject<?>)value).getData()));
      }
      else if (value instanceof Map)
      {
        resultB.add(strip((Map<String, Object>)value));
      }
      else if (value instanceof List)
      {
        resultB.add(strip((List)value));
      }
      else if (value instanceof Set)
      {
        resultB.add(strip((Set)value));
      }
      else if (value instanceof Collection)
      {
        resultB.add(strip((Collection)value));
      }
      else
      {
        resultB.add(value);
      }
    }
    return resultB.build();
  }
  private ImmutableSet<Object> strip(final Set<Object> set)
  {
    final ImmutableSet.Builder<Object> resultB = ImmutableSet.builder();
    for (final Object value : set)
    {
      if (value instanceof WObject<?>)
      {
        resultB.add(new WObject(((WObject<?>)value).getData()));
      }
      else if (value instanceof Map)
      {
        resultB.add(strip((Map<String, Object>)value));
      }
      else if (value instanceof List)
      {
        resultB.add(strip((List)value));
      }
      else if (value instanceof Set)
      {
        resultB.add(strip((Set)value));
      }
      else if (value instanceof Collection)
      {
        resultB.add(strip((Collection)value));
      }
      else
      {
        resultB.add(value);
      }
    }
    return resultB.build();
  }

//  // Recursively deep-immutify data
//  private ImmutableSortedMap<String, Object> immutify(final Map<String, Object> map)
//  {
//    final ImmutableSortedMap.Builder<String, Object> result = ImmutableSortedMap.naturalOrder();
//    for (final Map.Entry<String, Object> entry : map.entrySet())
//    {
//      if (entry.getValue() instanceof Map)
//      {
//        result.put(entry.getKey(), immutify((Map<String, Object>)entry.getValue()));
//      }
//      else if (entry.getValue() instanceof List)
//      {
//        result.put(entry.getKey(), immutify((List)entry.getValue()));
//      }
//      else
//      {
//        result.put(entry);
//      }
//    }
//    return result.build();
//  }
//
//  private ImmutableList<Object> immutify(final List<Object> list)
//  {
//    final ImmutableList.Builder<Object> result = ImmutableList.builder();
//    for (final Object value : list)
//    {
//      if (value instanceof Map)
//      {
//        result.add(immutify((Map<String, Object>)value));
//      }
//      else if (value instanceof List)
//      {
//        result.add(immutify((List)value));
//      }
//      else
//      {
//        result.add(value);
//      }
//    }
//    return result.build();
//  }

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
    final Class requiredClass = (Class)(typeRef.getType() instanceof ParameterizedType ? ((ParameterizedType)typeRef.getType()).getRawType() : typeRef.getType());
    final boolean isCollection = Collection.class.isAssignableFrom(requiredClass);
    if (val == null)
    {
      return Optional.absent();
    }

    if (Objects.equal(requiredClass, val.getClass()))
    {
      return Optional.of((U)val);
    }
    final String valStr = stringify(val, isCollection);
    try
    {
      final U result = MAPPER.readValue(valStr, typeRef);
      // It is possible that data has not been initialised yet.  This is because we can call this method from preCreate()
      if (data != null)
      {
        data.put(key, result);
      }
      return Optional.of(result);
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return Optional.absent();
    }
  }

  @JsonIgnore
  public <U> Optional<U> getScratch(final String key)
  {
    return Optional.fromNullable((U)scratchData.get(key));
  }

  @JsonIgnore
  public <U> void setScratch(final String key, final U obj)
  {
    scratchData.put(key, obj);
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
      // It is possible that data has not been initialised yet.  This is because we can call this method from preCreate()
      if (data != null)
      {
        data.put(key, result);
      }
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

    // Check for a type which should be passed through without quoting.  Valid JSON would be a boolean or a number, and we add
    // enums to the list
    if (val instanceof Enum<?> ||
        val instanceof Boolean ||
        val instanceof Number)
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

  @JsonIgnore
  private volatile String stringRepresentation = null;

  @Nullable
  public static String serialize(@Nullable final WObject<?> obj)
  {
    if (obj == null)
    {
      return null;
    }
    try
    {
      return MAPPER.writeValueAsString(obj);
    }
    catch (JsonProcessingException e)
    {
      throw new ServerError("Failed to serialize object: ", e);
    }
  }

  @Nullable
  public static <T> T deserialize(@Nullable final String val, final Class<T> klazz)
  {
    if (val == null)
    {
      return null;
    }
    try
    {
      return MAPPER.readValue(val, klazz);
    }
    catch (IOException e)
    {
      return null;
    }

  }

  /**
   * <em>N.B.</em>The string representation of the data does not show internal fields.  As such it should not be used as a way of
   * transmitting or storing the object
   */
  @Override
  public String toString()
  {
    String result = stringRepresentation;
    if (result == null)
    {
      try
      {
        result = MAPPER.writeValueAsString(externalData());
      }
      catch (final JsonProcessingException e)
      {
        System.err.println("Failed to generate string");
        return "{\"error\":\"toString() failed: " + e.getMessage() + "\"}";
      }
      stringRepresentation = result;
    }
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object that)
  {
    return that instanceof WObject && this.hashCode() == that.hashCode() && this.compareTo((T) that) == 0;
  }

  private ImmutableMap<String, Object> externalData()
  {
    return strip(this.data);
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
      result = Objects.hashCode(toString());
      hashCode = result;
    }

    return result;
  }

  public int compareTo(@Nonnull T that)
  {
    // We cannot compare the objects directly because a native object might contain, for example,
    // a datetime whereas the deserialized object will contain a serialized String of that datetime.
    // The safest way to compare is to turn them both in to simple strings and compare them, although
    // this might not be cheap.  We use toString(), which caches the data so we only do it once
    // We also want to remove any internal fields, as they don't count when carrying out comparisons
    return ComparisonChain.start().compare(this.toString(), that.toString()).result();
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

    public P id(final WID<? extends T> id)
    {
      this.data.put(ID, id);
      return self();
    }

    public P data(final Map<String, ? extends Object> data)
    {
      this.data.putAll(data);
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
  }
}
