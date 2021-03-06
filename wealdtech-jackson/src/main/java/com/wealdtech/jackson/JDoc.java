/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
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
 * JDoc is a JSON document which serializes without altering its structure. At current JDoc does not store any type information so
 * this needs to be contained externally.
 */
public class JDoc implements Comparable<JDoc>, Map<String, Object>
{
  private static final Logger LOG = LoggerFactory.getLogger(JDoc.class);

  public static final JDoc EMPTY = new JDoc(ImmutableMap.<String, Object>of());

  @JsonProperty
  protected final ImmutableMap<String, Object> data;

  @JsonCreator
  public JDoc(final ImmutableMap<String, Object> data)
  {
    this.data = data;
  }

  @JsonIgnore
  public ImmutableMap<String, Object> getData()
  {
    return this.data;
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
   * Overlay another JDoc on top of this one, updating where required
   *
   * @param overlay another JDoc
   *
   * @return the combined JDoc
   */
  public JDoc overlay(final Optional<JDoc> overlay)
  {
    if (!overlay.isPresent())
    {
      return this;
    }
    final Map<String, Object> data = Maps.newHashMap();
    data.putAll(data);
    data.putAll(overlay.get().data);

    return new JDoc(ImmutableMap.copyOf(data));
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
    return that instanceof JDoc && this.hashCode() == that.hashCode() && this.compareTo((JDoc)that) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.data);
  }

  private static final MapComparator<String, Object> MAP_COMPARATOR = new MapComparator<>();

  public int compareTo(@Nonnull JDoc that)
  {
    return ComparisonChain.start().compare(this.data, that.data, MAP_COMPARATOR).result();
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
}
