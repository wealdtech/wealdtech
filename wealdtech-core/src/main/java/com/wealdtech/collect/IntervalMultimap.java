/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.collect;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;

import javax.annotation.Nullable;

import java.util.*;

import static com.wealdtech.Preconditions.checkNotNull;

public class IntervalMultimap<T extends Comparable, U> implements Multimap<Range<T>, U>
{
  private TreeMap<T, IntervalMapEvents<U>> entries;
  private int size;

  public IntervalMultimap()
  {
    entries = new TreeMap<>();
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
    return false;
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
  public boolean containsEntry(@Nullable final Object key, @Nullable final Object value)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String dump()
  {
    return entries.toString();
  }

  @Override
  public boolean put(@Nullable final Range<T> key, @Nullable final U value)
  {
    checkNotNull(key, "Key cannot be null");
    checkNotNull(value, "Value cannot be null");

    final T lower = key.lowerEndpoint();
    final T upper = key.upperEndpoint();

    // Get a submap of all existing entries in our range
    final NavigableMap<T, IntervalMapEvents<U>> subEntries;
    subEntries = entries.subMap(lower, true, upper, true);

    // For each entry we note that this item is present at the given point
    for (final Map.Entry<T, IntervalMapEvents<U>> entry : subEntries.entrySet())
    {
      boolean added = false;
      if (entry.getKey().equals(lower))
      {
        entry.getValue().starts.add(value);
        added = true;
      }
      if (entry.getKey().equals(upper))
      {
        entry.getValue().ends.add(value);
        added = true;
      }
      if (!added)
      {
        entry.getValue().exists.add(value);
      }
    }

    // If we didn't have an entry when we started we need to add it in now
    if (subEntries.get(lower) == null)
    {
      final IntervalMapEvents<U> startEvent = new IntervalMapEvents<>();
      startEvent.starts.add(value);
      final Map.Entry<T, IntervalMapEvents<U>> priorEntry = entries.lowerEntry(lower);
      if (priorEntry != null)
      {
        startEvent.exists.addAll(priorEntry.getValue().starts);
        startEvent.exists.addAll(priorEntry.getValue().exists);
      }
      entries.put(key.lowerEndpoint(), startEvent);
    }

    // If we didn't have an entry when we ended we need to add it in now
    if (subEntries.get(upper) == null)
    {
      final IntervalMapEvents<U> endEvent = new IntervalMapEvents<>();
      endEvent.ends.add(value);
      final Map.Entry<T, IntervalMapEvents<U>> nextEntry = entries.higherEntry(upper);
      if (nextEntry != null)
      {
        endEvent.exists.addAll(nextEntry.getValue().ends);
        endEvent.exists.addAll(nextEntry.getValue().exists);
      }
      entries.put(key.upperEndpoint(), endEvent);
    }

    size++;
    return true;
  }

  @Override
  public boolean remove(@Nullable final Object key, @Nullable final Object value)
  {
    throw new UnsupportedOperationException("Cannot remove items");
  }

  @Override
  public boolean putAll(@Nullable final Range<T> key, final Iterable<? extends U> values)
  {
    for (final U value : values)
    {
      put(key, value);
    }
    return true;
  }

  @Override
  public boolean putAll(final Multimap<? extends Range<T>, ? extends U> multimap)
  {
    for (final Map.Entry<? extends Range<T>, ? extends Collection<? extends U>> entry : multimap.asMap().entrySet())
    {
      putAll(entry.getKey(), entry.getValue());
    }
    return true;
  }

  @Override
  public Collection<U> replaceValues(@Nullable final Range<T> key, final Iterable<? extends U> values)
  {
    throw new UnsupportedOperationException("Cannot replace items");
  }

  @Override
  public Collection<U> removeAll(@Nullable final Object key)
  {
    throw new UnsupportedOperationException("Cannot remove items");
  }

  @Override
  public void clear()
  {
    entries.clear();
    size = 0;
  }

  @Override
  public Collection<U> get(@Nullable final Range<T> key)
  {
    checkNotNull(key, "Key cannot be null");

    // Find our lower and upper ranges
    final T lower = key.lowerEndpoint();
    final T upper = key.upperEndpoint();
    final T lowerRange = MoreObjects.firstNonNull(entries.floorKey(lower), key.lowerEndpoint());
    final T upperRange = MoreObjects.firstNonNull(entries.floorKey(upper), key.upperEndpoint());

    // Get a sub-map of all existing entries in our range
    final NavigableMap<T, IntervalMapEvents<U>> subEntries;
    subEntries = entries.subMap(lowerRange, true, upperRange, true);
    final Set<U> results = Sets.newHashSet();

    for (final Map.Entry<T, IntervalMapEvents<U>> subEntry : subEntries.entrySet())
    {
      if (!subEntry.getKey().equals(upper))
      {
        results.addAll(subEntry.getValue().starts);
      }
      results.addAll(subEntry.getValue().exists);
      if (!subEntry.getKey().equals(lower))
      {
        results.addAll(subEntry.getValue().ends);
      }
    }

    return results;
  }

  @Override
  public Set<Range<T>> keySet()
  {
    Set<Range<T>> keys = Sets.newHashSet();
    // Have to create a set of ranges from the keys in the treemap
    T lastKey = null;
    for (final T key : entries.keySet())
    {
      if (lastKey != null)
      {
        keys.add(Range.closedOpen(lastKey, key));
      }
      lastKey = key;
    }
    return keys;
  }

  @Override
  public Multiset<Range<T>> keys()
  {
    throw new UnsupportedOperationException("Cannot obtain keys");
  }

  @Override
  public Collection<U> values()
  {
    throw new UnsupportedOperationException("Cannot obtain values");
  }

  @Override
  public Collection<Map.Entry<Range<T>, U>> entries()
  {
    throw new UnsupportedOperationException("Cannot obtain entries");
  }

  @Override
  public Map<Range<T>, Collection<U>> asMap()
  {
    throw new UnsupportedOperationException("Cannot obtain map");
  }

  private static class IntervalMapEvents<U>
  {
    public Set<U> starts;
    public Set<U> exists;
    public Set<U> ends;

    public IntervalMapEvents()
    {
      this.starts = Sets.newHashSet(); // The first key for which this item exists
      this.exists = Sets.newHashSet();
      this.ends = Sets.newHashSet(); // The last key for which this item exists
    }

    @Override
    public String toString()
    {
      return "starts=" + starts + ",exists=" + exists + ",ends=" + ends;
    }
  }

}
