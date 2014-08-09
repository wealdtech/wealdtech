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

import com.google.common.collect.ImmutableSet;
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

    List<V> endArray = endMap.get(key.upperEndpoint());
    if (endArray == null)
    {
      endArray = new ArrayList<>();
      endMap.put(key.upperEndpoint(), endArray);
    }
    endArray.add(value);

    size++;
    return true;
  }

  @Override
  public Collection<V> get(final Range<K> range)
  {
    // Find all items which start before this range ends
    ImmutableSet.Builder<V> startersB = ImmutableSet.builder();
    Map.Entry<K, List<V>> startEntry = startMap.floorEntry(range.upperEndpoint());
    while (startEntry != null)
    {
      // Because our range is [) we don't include anything on the upper endpoint itself
      if (!startEntry.getKey().equals(range.upperEndpoint()))
      {
        startersB.addAll(startEntry.getValue());
      }
      startEntry = startMap.lowerEntry(startEntry.getKey());
    }
    final ImmutableSet<V> starters = startersB.build();

    // Final all items which end after this range starts
    ImmutableSet.Builder<V> finishersB = ImmutableSet.builder();
    Map.Entry<K, List<V>> finishEntry = endMap.ceilingEntry(range.lowerEndpoint());
    while (finishEntry != null)
    {
      // Because our range is [) we don't include anything on the lower endpoint itself
      if (!finishEntry.getKey().equals(range.lowerEndpoint()))
      {
        finishersB.addAll(finishEntry.getValue());
      }
      finishEntry = endMap.higherEntry(finishEntry.getKey());
    }
    final ImmutableSet<V> finishers = finishersB.build();

    // Our result is everything which is in both sets
    return Sets.intersection(starters, finishers);
  }
}
