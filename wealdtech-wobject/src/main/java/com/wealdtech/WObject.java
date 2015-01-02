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
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Maps;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.MapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * The Weald Technology object. A generic object which allows for arbitrary storage of data
 */
public class WObject<T extends WObject<?>> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  @JsonIgnore
  private final Map<String, Object> data;

  @JsonCreator
  public WObject(final Map<String, Object> data)
  {
    this.data = MoreObjects.firstNonNull(data, Maps.<String, Object>newHashMap());
  }

  public boolean exists(final String name)
  {
    return data.containsKey(name);
  }

  @JsonIgnore
  @Nullable
  public <U> U get(final String name, final Class<U> klazz)
  {
    if (!data.containsKey(name))
    {
      return null;
    }

    final String obj = stringify(data.get(name));
    try
    {
      return WealdMapper.getMapper().readValue(obj, klazz);
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return null;
    }
  }

  @JsonIgnore
  @Nullable
  public <U> U get(final String name, final TypeReference<U> typeRef)
  {
    if (!data.containsKey(name))
    {
      return null;
    }

    final String obj = stringify(data.get(name));
    try
    {
      return WealdMapper.getMapper().readValue(obj, typeRef);
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to parse value: ", ioe);
      return null;
    }
  }

  private String stringify(final Object obj)
  {
    String valStr;
    if (obj instanceof String)
    {
      valStr = (String)obj;
    }
    else
    {
      try
      {
        valStr = WealdMapper.getMapper().writeValueAsString(obj);
      }
      catch (final IOException ioe)
      {
        LOG.error("Failed to encode value: ", ioe);
        return null;
      }
    }
    if (obj instanceof Enum<?>)
    {
      return valStr;
    }
    if (!valStr.startsWith("{") && !valStr.startsWith("["))
    {
      valStr = "\"" + valStr + "\"";
    }
    return valStr;
  }

  @JsonAnyGetter
  protected Map<String, Object> any()
  {
    return data;
  }

  @JsonAnySetter
  protected void set(final String name, final Object value)
  {
    data.put(name, value);
  }

  public static class Builder<P extends Builder<P>>
  {
    protected Map<String, Object> data;

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

  // Standard object methods follow
  @Override
  public String toString()
  {
    try
    {
      return WealdMapper.getMapper().writeValueAsString(this);
    }
    catch (final JsonProcessingException e)
    {
      LOG.error("Failed to create JSON for object: ", e);
      return "Bad";
    }
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(data);
  }

  @Override
  public boolean equals(final Object that)
  {
    return that instanceof WObject && this.compareTo((T)that) == 0;
  }

  private static final MapComparator<String, Object> MAP_COMPARATOR = new MapComparator<>();

  @Override
  public int compareTo(@Nonnull final T that)
  {
    return ComparisonChain.start().compare(this.any(), that.any(), MAP_COMPARATOR).result();
  }
}
