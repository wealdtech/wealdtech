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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.wealdtech.DataError;
import com.wealdtech.utils.GuavaUtils;
import com.wealdtech.utils.MapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JDoc is a JSON document which serializes without altering its structure. At current JDoc does not store any type information so
 * this needs to be contained externally.
 * This relies on JDocSerializer and JDocDeserializer to work correctly; it won't serialize/deserialize properly without them.
 */
public class JDoc implements Comparable<JDoc>, Map<String, Object>
{
  private static final Logger LOG = LoggerFactory.getLogger(JDoc.class);

  private static final JDoc EMPTY = new JDoc(ImmutableMap.<String, Object>of());

  @JsonProperty
  private final ImmutableMap<String, Object> data;

  @JsonCreator
  public JDoc(final ImmutableMap<String, Object> data)
  {
    this.data = data;
  }

  public static JDoc cleanJDoc()
  {
    return EMPTY;
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <T> Optional<T> get(final String key, final TypeReference<T> typeRef)
  {
    LOG.trace("Attempting to fetch {} as {}", key, typeRef.getType());
    final Object val = data.get(key);
    return resolve(val, typeRef);
  }

  private <T> Optional<T> resolve(final Object val, final TypeReference<T> typeRef)
  {
    if (val != null)
    {
      try
      {
        LOG.trace("Attempting to parse {} as {}", val, typeRef.getType());
        if (val instanceof JDoc)
        {
          if (val.getClass().equals(JDoc.class))
          {
            return Optional.of((T)val);
          }
          else
          {
            // Need to upcast
            try
            {
              return Optional.of((T)((Class)typeRef.getType()).getConstructor(ImmutableMap.class)
                                                              .newInstance(((JDoc)val).getData()));
            }
            catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e)
            {
              LOG.error("Failed to upcast jdoc: ", e);
              return Optional.absent();
            }
          }
        }
        else if (val instanceof Collection)
        {
          // Need to create collection
          Collection collection;
          if (val instanceof List)
          {
            collection = Lists.newArrayList();
          }
          else
          {
            collection = Sets.newHashSet();
          }
          for (final Object collectionVal : (Collection)val)
          {
            // Need to find out the class of whatever it is we're resolving to
            final Class<?> klazz = Class.forName(((ParameterizedType)typeRef.getType()).getActualTypeArguments()[0].toString()
                                                                                                                   .substring(6));
            final Optional<?> resolved = resolve(collectionVal, klazz);
            if (resolved.isPresent())
            {
              collection.add(resolved.get());
            }
          }
          return Optional.of((T)collection);
        }
        else if (val instanceof Map)
        {
          return Optional.of((T)val);
        }
        else
        {
          return Optional.fromNullable((T)WealdMapper.getMapper().readValue("\"" + val + "\"", typeRef));
        }
      }
      catch (final IOException ioe)
      {
        throw new DataError.Bad("Failed to parse data: ", ioe);
      }
      catch (ClassNotFoundException e)
      {
        throw new DataError.Bad("Failed to find class: ", e);
      }
    }
    return Optional.absent();
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <T> Optional<T> get(final String key, final Class<T> klazz)
  {
    LOG.trace("Attempting to fetch {} as {}", key, klazz.toString());
    final Object val = data.get(key);
    return resolve(val, klazz);
  }

  @JsonIgnore
  private <T> Optional<T> resolve(final Object val, final Class<T> klazz)
  {
    if (val == null)
    {
      LOG.trace("No such key");
      return Optional.absent();
    }
    else
    {
      try
      {
        LOG.trace("Attempting to parse {} ({}) as {}", val, val.getClass().getSimpleName(), klazz.toString());
        if (val instanceof JDoc)
        {
          // Need to upcast
          try
          {
            final String dataStr = WealdMapper.getMapper().writeValueAsString(((JDoc)val).getData());
            LOG.trace("data string is {}", dataStr);
            final T upcast = WealdMapper.getMapper().readValue(dataStr, klazz);
            if (upcast != null)
            {
              LOG.trace("Upcasted value is {} ({})", upcast, upcast.getClass().getSimpleName());
              return Optional.of(upcast);
            }
            else
            {
              return Optional.absent();
            }
          }
          catch (final Exception e)
          {
            LOG.error("Failed to upcast jdoc: ", e);
            return Optional.absent();
          }
        }
        if (val instanceof Map)
        {
          // Need to upcast
          try
          {
            final String dataStr = WealdMapper.getMapper().writeValueAsString((Map)val);
            LOG.trace("data string is {}", dataStr);
            final T upcast = WealdMapper.getMapper().readValue(dataStr, klazz);
            if (upcast != null)
            {
              LOG.trace("Upcasted value is {} ({})", upcast, upcast.getClass().getSimpleName());
              return Optional.of(upcast);
            }
            else
            {
              return Optional.absent();
            }
          }
          catch (final Exception e)
          {
            LOG.error("Failed to upcast jdoc: ", e);
            return Optional.absent();
          }
        }
        else if (val instanceof Collection)
        {
          return Optional.of((T)val);
        }
        else
        {
          return Optional.fromNullable(WealdMapper.getMapper().readValue("\"" + val + "\"", klazz));
        }
      }
      catch (final IOException ioe)
      {
        throw new DataError.Bad("Failed to parse data: ", ioe);
      }
    }
  }

  /**
   * @return the data in this JDoc
   */
  @JsonIgnore
  public ImmutableMap<String, Object> getData()
  {
    return this.data;
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
    return MoreObjects.toStringHelper(this).add("data", GuavaUtils.emptyToNull(data)).omitNullValues().toString();
  }

  @Override
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
