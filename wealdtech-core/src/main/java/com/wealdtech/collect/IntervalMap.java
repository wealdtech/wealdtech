/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
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
import java.util.*;

import static com.wealdtech.Preconditions.checkState;

/**
 * An IntervalMap is a form of map that provides details of which value falls within which ranges.
 * @param <T> The type of the map keys; always supplied as ranges
 * @param <U> The type of the values
 */
public class IntervalMap<T extends Comparable, U> implements Map<Range<T>, U>
{
  private NavigableMap<T, TwoTuple<T, U>> entries;

  public IntervalMap()
  {
    entries = new TreeMap<>();
  }

  @Override
  public int size()
  {
    return entries.size();
  }

  @Override
  public boolean isEmpty()
  {
    return entries.isEmpty();
  }

  @Override
  public boolean containsKey(@Nullable final Object key)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public boolean containsValue(@Nullable final Object value)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public U get(final Object key)
  {
    final T actualKey = (T)key;
    Entry<T, TwoTuple<T, U>> entry = entries.floorEntry(actualKey);
    while (entry != null)
    {
      if (entry.getKey().equals(actualKey))
      {
        // We have a direct hit on the start of an entry
        return entry.getValue().getT();
      }
      else if (actualKey.compareTo(entry.getValue().getS()) < 0)
      {
        // We are within an entry's bounds
        return entry.getValue().getT();
      }
      else if (actualKey.compareTo(entry.getKey()) < 0)
      {
        // We have fallen below the entry's bounds, give up
        return null;
      }
      // It is possible that there is an entry that starts lower than the current one that applies, so grab it to test
      entry = entries.lowerEntry(entry.getKey());
    }
    return null;
  }

  @Override
  public U put(final Range<T> key, final U value)
  {
    checkState(key.hasLowerBound(), "Key must have a lower bound");
    checkState(key.hasUpperBound(), "Key must have an upper bound");
    TwoTuple<T, U> oldEntry = entries.put(key.lowerEndpoint(), new TwoTuple<>(key.upperEndpoint(), value));
    return oldEntry == null ? null : oldEntry.getT();
  }

  @Override
  public U remove(final Object key)
  {
    final Range<T> actualKey = (Range<T>)key;
    final TwoTuple<T, U> oldEntry = entries.get(actualKey.lowerEndpoint());
    return oldEntry == null ? null : oldEntry.getT();
  }

  @Override
  public void putAll(final Map<? extends Range<T>, ? extends U> m)
  {
    throw new ServerError("Not implemented");
  }

  @Override
  public void clear()
  {
    entries.clear();
  }

  @Override
  public Set<Range<T>> keySet()
  {
    final Set<Range<T>> results = new HashSet<>();
    for (final Entry<T, TwoTuple<T, U>> entry : entries.entrySet())
    {
      results.add(Range.closedOpen(entry.getKey(), entry.getValue().getS()));
    }
    return results;
  }

  @Override
  public Collection<U> values()
  {
    final Set<U> results = new HashSet<>();
    for (final TwoTuple<T, U> value : entries.values())
    {
      results.add(value.getT());
    }
    return results;
  }

  @Override
  public Set<Entry<Range<T>, U>> entrySet()
  {
    throw new UnsupportedOperationException("Not supported");
//    final Set<Entry<Range<T>, U>> results = new HashSet<>();
//    for (final Entry<T, TwoTuple<T, U>> entry : entries.entrySet())
//    {
//      results.add(new HashMap.Entry<>(Range.closedOpen(entry.getKey(), entry.getValue().getS()), entry.getValue().getT()));
//    }
//    return results;
  }
}
