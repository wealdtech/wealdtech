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

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.util.*;

/**
 */
public class TreeRangedMultimap<K extends Comparable<K>, V> implements RangedMultimap<K, V>
{
  private TreeMap<K, List<V>> underlying;
  private int size;

  public TreeRangedMultimap()
  {
    underlying = new TreeMap<>();
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
    return underlying.isEmpty();
  }

  @Override
  public boolean put(final Range<K> key, final V value)
  {
    List<V> valArray = underlying.get(key.lowerEndpoint());
    if (valArray == null)
    {
      valArray = new ArrayList<>();
      underlying.put(key.lowerEndpoint(), valArray);
    }

    valArray.add(value);
    size++;
    return true;
  }

  @Override
  public Collection<V> get(final K key)
  {
    Map.Entry<K, List<V>> curEntry = underlying.floorEntry(key);
    return underlying.get(key);
  }

  @Override
  public Collection<V> get(final Range<K> range)
  {
    List<V> allResults = Lists.newArrayList();
    Map.Entry<K, List<V>> curEntry = underlying.floorEntry(range.lowerEndpoint());
    if (curEntry == null)
    {
      curEntry = underlying.ceilingEntry(range.lowerEndpoint());
    }
    while (curEntry != null && curEntry.getKey().compareTo(range.upperEndpoint()) < 0)
    {
      allResults.addAll(curEntry.getValue());
      curEntry = underlying.higherEntry(curEntry.getKey());
    }
    return allResults;
  }
}
