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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.jackson.WealdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Weald Technology object
 * A generic immutable object which allows for arbitrary storage of data, serialization and deserialization through
 * Jackson, and object validation
 */
public class WObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  public static final WObject<?> EMPTY = new WObject(ImmutableMap.<String, Object>of());

  @JsonIgnore
  protected final ImmutableMap<String, Object> data;

  @JsonAnyGetter
  private ImmutableMap<String, Object> any() {
    return data;
  }

  @JsonCreator
  public WObject(final Map<String, Object> data)
  {
    this.data = ImmutableSortedMap.copyOf(preCreate(data));
    validate();
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

    return new WObject<>(data);
  }

  public boolean exists(final String key)
  {
    return data.containsKey(key);
  }

  @Override
  public String toString()
  {
    try {
      return WealdMapper.getServerMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      System.err.println("Failed to generate string");
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object that)
  {
    return that instanceof WObject && this.compareTo((T) that) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.data);
  }

  public int compareTo(@Nonnull T that)
  {
    // We cannot compare the objects directly because a native object might contain, for example,
    // a datetime whereas the deserialized object will contain a serialized String of that datetime.
    // The safest way to compare is to turn them both in to simple strings and compare them, altough
    // this might not be cheap
    return ComparisonChain.start().compare(this.toString(), that.toString()).result();
  }

  public static class Builder<T extends WObject<T>, P extends Builder<T, P>>
  {
    protected HashMap<String, Object> data;

    public Builder(final T prior)
    {
      data = Maps.newHashMap();
      data.putAll(prior.data);
    }

    public Builder()
    {
      data = Maps.newHashMap();
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
}
