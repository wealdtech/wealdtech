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
import com.google.common.collect.Sets;

import java.util.*;

/**
 * A ranged multimap using {@link TreeMap} as the underlying storage mechanism
 */
public class TreeRangedMultimap<K extends Comparable<? super K>, V> implements RangedMultimap<K, V>
{
  private TreeMap<K, List<V>> startMap;
  private TreeMap<K, List<V>> endMap;

  private int size;

  public TreeRangedMultimap()
  {
    startMap = new TreeMap<>();
    endMap = new TreeMap<>();
    size = 0;
  }

  @Override
  public int size()
  {
    return size;
  }

  @Override
  public boolean isEmpty()
  {
    return startMap.isEmpty();
  }

  @Override
  public boolean put(final Range<K> key, final V value)
  {
    List<V> startArray = startMap.get(key.lowerEndpoint());
    if (startArray == null)
    {
      startArray = new ArrayList<>();
      startMap.put(key.lowerEndpoint(), startArray);
    }
    startArray.add(value);

    List<V> endArray = startMap.get(key.upperEndpoint());
    if (endArray == null)
    {
      endArray = new ArrayList<>();
      startMap.put(key.upperEndpoint(), endArray);
    }
    endArray.add(value);

    size++;
    return true;
  }

  @Override
  public Collection<V> get(final K key)
  {
    Map.Entry<K, List<V>> curEntry = startMap.floorEntry(key);
    return curEntry == null ? null : curEntry.getValue();
  }

  @Override
  public Collection<V> get(final Range<K> range)
  {
    Set<V> allResults = Sets.newHashSet();

    // Add all items which start in this range...
    Map.Entry<K, List<V>> startEntry = startMap.ceilingEntry(range.lowerEndpoint());
    while (startEntry != null && startEntry.getKey().compareTo(range.upperEndpoint()) < 0)
    {
      allResults.addAll(startEntry.getValue());
      startEntry =  startMap.higherEntry(startEntry.getKey());
    }

    // ...and which and in this range
    Map.Entry<K, List<V>> endEntry = startMap.floorEntry(range.upperEndpoint());
    while (endEntry != null && endEntry.getKey().compareTo(range.lowerEndpoint()) > 0)
    {
      allResults.addAll(endEntry.getValue());
      endEntry =  startMap.lowerEntry(endEntry.getKey());
    }

    return allResults;
  }
}
