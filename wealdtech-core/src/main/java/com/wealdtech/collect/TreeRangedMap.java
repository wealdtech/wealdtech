/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.collect;

import com.google.common.collect.Range;
import com.wealdtech.ServerError;
import com.wealdtech.TwoTuple;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * RangedMap using TreeMap as a backing store
 */
public class TreeRangedMap<K extends Comparable<K>, V> implements RangedMap<K, V>
{
  private final TreeMap<K, TwoTuple<Range<K>, V>> underlying;

  public TreeRangedMap()
  {
    underlying = new TreeMap<>();
  }

  @Nullable
  @Override
  public V get(final K key)
  {
    final Map.Entry<K, TwoTuple<Range<K>, V>> floorEntry = underlying.floorEntry(key);
    if (floorEntry != null && floorEntry.getValue().getS().contains(key))
    {
      return floorEntry.getValue().getT();
    }
    else
    {
      return null;
    }
  }

  @Override
  public List<TwoTuple<Range<K>, V>> get(final Range<K> range)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }

  @Nullable
  @Override
  public Map.Entry<Range<K>, V> getEntry(final K key)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }

  @Override
  public void put(final Range<K> key, final V value)
  {
    // Find any overlapping ranges.  Because we base our key on the start of the range we might need to see if our start is in the
    // middle of an existing range
    final K lowerEndpoint;
    final Map.Entry<K, TwoTuple<Range<K>, V>> floorEntry = underlying.floorEntry(key.lowerEndpoint());

    if (floorEntry != null && floorEntry.getValue().getS().upperEndpoint().compareTo(key.lowerEndpoint()) < 0)
    {
      lowerEndpoint = floorEntry.getKey();
    }
    else
    {
      lowerEndpoint = key.lowerEndpoint();
    }

    final NavigableMap<K, TwoTuple<Range<K>, V>> subMap = underlying.subMap(lowerEndpoint, true, key.upperEndpoint(), true);
    System.err.println(subMap);
    underlying.put(key.lowerEndpoint(), new TwoTuple<>(key, value));
  }

  @Override
  public void putAll(final RangedMap<K, V> rangedMap)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }

  @Override
  public void clear()
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }

  @Override
  public void remove(final K key)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }

  @Override
  public void remove(final Range<K> range)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }
}
