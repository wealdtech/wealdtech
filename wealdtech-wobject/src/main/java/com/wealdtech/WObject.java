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
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.GuavaUtils;
import com.wealdtech.utils.MapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Weald Technology object
 * A generic immutable object which allows for arbitrary storage of data, serialization and deserialization through
 * Jackson, and object validation
 */
public class WObject<T> implements Comparable<T>, Map<String, Object>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  public static final WObject<?> EMPTY = new WObject(ImmutableMap.<String, Object>of());

  @JsonProperty
  protected final ImmutableMap<String, Object> data;

  @JsonCreator
  public WObject(final ImmutableMap<String, Object> data)
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

    return new WObject<>(ImmutableMap.copyOf(data));
  }

  @JsonIgnore
  @Override
  public int size()
  {
    return data.size();
  }

  @JsonIgnore
  @Override
  public boolean isEmpty()
  {
    return data.isEmpty();
  }

  @JsonIgnore
  @Override
  public boolean containsKey(final Object key)
  {
    return data.containsKey(key);
  }

  @JsonIgnore
  @Override
  public boolean containsValue(final Object value)
  {
    return data.containsValue(value);
  }

  @JsonIgnore
  @Override
  public Object get(final Object key)
  {
    return get(key.toString(), Object.class).orNull();
  }

  @Override
  public Object put(final String key, final Object value)
  {
    throw new UnsupportedOperationException("Not allowed");
  }

  @JsonIgnore
  @Override
  public Object remove(final Object key)
  {
    throw new UnsupportedOperationException("Not allowed");
  }

  @Override
  public void putAll(@Nonnull final Map<? extends String, ?> m)
  {
    throw new UnsupportedOperationException("Not allowed");
  }

  @JsonIgnore
  @Override
  public void clear()
  {
    throw new UnsupportedOperationException("Not allowed");
  }

  @Override
  public
  @Nonnull
  Set<String> keySet()
  {
    return data.keySet();
  }

  @Override
  public
  @Nonnull
  Collection<Object> values()
  {
    return data.values();
  }

  @Override
  public
  @Nonnull
  Set<Entry<String, Object>> entrySet()
  {
    return data.entrySet();
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this).addValue(GuavaUtils.emptyToNull(data)).omitNullValues().toString();
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
