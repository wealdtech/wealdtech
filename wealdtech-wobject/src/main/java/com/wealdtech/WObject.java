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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.GuavaUtils;
import com.wealdtech.utils.MapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The Weald Technology object
 * A generic immutable object which allows for arbitrary storage of data, serialization and deserialization through
 * Jackson, and object validation
 */
public class WObject<T extends WObject<?>> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  @JsonIgnore
  public static final WObject<?> EMPTY = new WObject(ImmutableMap.<String, Object>of());

  @JsonUnwrapped
  @JsonProperty
  protected final ImmutableMap<String, Object> data;

  @JsonCreator
  public WObject(final ImmutableMap<String, Object> data)
  {
    this.data = preCreate(data);
    validate();
  }

  /**
   * Carry out any operations required to manage the object prior to creation.  For example, this could add a
   * timestamp or a version number.
   *
   * @param data the data supplied
   * @return the data to be used in creation of the object
   */
  protected ImmutableMap<String, Object> preCreate(final ImmutableMap<String, Object> data) { return data; }

  /**
   * Validate the data in the object to ensure that it conforms to whatever requirements it has.
   * This should throw a DataError if validation is not successful
   */
  protected void validate() {}

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
    final String valStr = stringify(val);
    try
    {
      return Optional.fromNullable((U)WealdMapper.getMapper().readValue(valStr, typeRef));
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
    final String valStr = stringify(val);
    try
    {
      return Optional.fromNullable(WealdMapper.getMapper().readValue(valStr, klazz));
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return Optional.absent();
    }
  }

  public boolean exists(final String key)
  {
    return data.containsKey(key);
  }

  private String stringify(final Object val)
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
        valStr = WealdMapper.getMapper().writeValueAsString(val);
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

    if (!valStr.startsWith("{") && !valStr.startsWith("["))
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
  public WObject<T> overlay(final Optional<WObject<T>> overlay)
  {
    if (!overlay.isPresent())
    {
      return this;
    }
    final Map<String, Object> data = Maps.newHashMap();
    data.putAll(data);
    data.putAll(overlay.get().data);

    return new WObject<>(ImmutableMap.copyOf(data));
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this).add("data", GuavaUtils.emptyToNull(data)).omitNullValues().toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object that)
  {
    return that instanceof WObject && this.hashCode() == that.hashCode() && this.compareTo((T)that) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.data);
  }

  @JsonIgnore
  private static final MapComparator<String, Object> MAP_COMPARATOR = new MapComparator<>();

  public int compareTo(@Nonnull T that)
  {
    return ComparisonChain.start().compare(this.data, that.data, MAP_COMPARATOR).result();
  }

  public static class Builder<P extends Builder<P>>
  {
    protected ImmutableMap.Builder<String, Object> data;

    public Builder()
    {
      data = ImmutableMap.builder();
    }

    public P data(final String name, final Object value)
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
//
//  @JsonIgnore
//  private final Map<String, Object> data;
//
//  @JsonCreator
//  public WObject(final Map<String, Object> data)
//  {
//    this.data = MoreObjects.firstNonNull(data, Maps.<String, Object>newHashMap());
//  }
//
//  public boolean exists(final String name)
//  {
//    return data.containsKey(name);
//  }
//
//  @JsonIgnore
//  @Nullable
//  public <U> U get(final String name, final Class<U> klazz)
//  {
//    if (!data.containsKey(name))
//    {
//      return null;
//    }
//
//    final String obj = stringify(data.get(name));
//    try
//    {
//      return WealdMapper.getMapper().readValue(obj, klazz);
//    }
//    catch (final IOException ioe)
//    {
//      LOG.error("Failed to parse value: ", ioe);
//      return null;
//    }
//  }
//
//  @JsonIgnore
//  @Nullable
//  public <U> U get(final String name, final TypeReference<U> typeRef)
//  {
//    if (!data.containsKey(name))
//    {
//      return null;
//    }
//
//    final String obj = stringify(data.get(name));
//    try
//    {
//      return WealdMapper.getMapper().readValue(obj, typeRef);
//    }
//    catch (final IOException ioe)
//    {
//      LOG.error("Failed to parse value: ", ioe);
//      return null;
//    }
//  }
//
//  private String stringify(final Object obj)
//  {
//    String valStr;
//    if (obj instanceof String)
//    {
//      valStr = (String)obj;
//    }
//    else
//    {
//      try
//      {
//        valStr = WealdMapper.getMapper().writeValueAsString(obj);
//      }
//      catch (final IOException ioe)
//      {
//        LOG.error("Failed to encode value: ", ioe);
//        return null;
//      }
//    }
//    if (obj instanceof Enum<?>)
//    {
//      return valStr;
//    }
//    if (!valStr.startsWith("{") && !valStr.startsWith("["))
//    {
//      valStr = "\"" + valStr + "\"";
//    }
//    return valStr;
//  }
//
//  @JsonAnyGetter
//  protected Map<String, Object> any()
//  {
//    return data;
//  }
//
//  @JsonAnySetter
//  protected void set(final String name, final Object value)
//  {
//    data.put(name, value);
//  }
//
//  public static class Builder<P extends Builder<P>>
//  {
//    protected Map<String, Object> data;
//
//    public Builder()
//    {
//      data = Maps.newHashMap();
//    }
//
//    public P data(final String name, final Object value)
//    {
//      this.data.put(name, value);
//      return self();
//    }
//
//    @SuppressWarnings("unchecked")
//    protected P self()
//    {
//      return (P)this;
//    }
//  }
//
//  // Standard object methods follow
//  @Override
//  public String toString()
//  {
//    try
//    {
//      return WealdMapper.getMapper().writeValueAsString(this);
//    }
//    catch (final JsonProcessingException e)
//    {
//      LOG.error("Failed to create JSON for object: ", e);
//      return "Bad";
//    }
//  }
//
//  @Override
//  public int hashCode()
//  {
//    return Objects.hashCode(data);
//  }
//
//  @Override
//  public boolean equals(final Object that)
//  {
//    return that instanceof WObject && this.compareTo((T)that) == 0;
//  }
//
//  private static final MapComparator<String, Object> MAP_COMPARATOR = new MapComparator<>();
//
//  @Override
//  public int compareTo(@Nonnull final T that)
//  {
//    return ComparisonChain.start().compare(this.any(), that.any(), MAP_COMPARATOR).result();
//  }
}
