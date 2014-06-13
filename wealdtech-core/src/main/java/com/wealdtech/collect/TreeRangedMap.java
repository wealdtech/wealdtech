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

import com.google.common.base.Objects;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.wealdtech.ServerError;
import com.wealdtech.TwoTuple;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
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
  public Map.Entry<K, TwoTuple<Range<K>, V>> getEntry(final K key)
  {
    final Map.Entry<K, TwoTuple<Range<K>, V>> entry = underlying.floorEntry(key);
    if (entry != null && entry.getValue().getS().contains(key))
    {
      return entry;
    }
    else
    {
      return null;
    }
  }

  @Override
  public int size()
  {
    return underlying.size();
  }

  /**
   * Validate a range prior to insertion
   * @param range the range to validate
   */
  private void validateRange(final Range<K> range)
  {
    if (!range.hasLowerBound())
    {
      throw new IllegalArgumentException("RangedMap must use ranges with defined lower bound");
    }
    if (!range.lowerBoundType().equals(BoundType.CLOSED))
    {
      throw new IllegalArgumentException("RangedMap must use ranges with closed lower bound");
    }
    if (!range.hasUpperBound())
    {
      throw new IllegalArgumentException("RangedMap must use ranges with defined upper bound");
    }
    if (!range.upperBoundType().equals(BoundType.OPEN))
    {
      throw new IllegalArgumentException("RangedMap must use ranges with open upper bound");
    }
  }

  @Override
  public void put(final Range<K> key, final V value)
  {
    validateRange(key);
    K resultantStart = key.lowerEndpoint();
    K resultantEnd = key.upperEndpoint();

    // Truncate or coalesce anything which overlaps the start of our new entry
    final Map.Entry<K, TwoTuple<Range<K>, V>> prior = getEntry(key.lowerEndpoint());
    if (prior != null)
    {
      if (prior.getValue().getT().equals(value))
      {
        // Values are the same so we can coalesce.
        if (resultantEnd.compareTo(prior.getValue().getS().upperEndpoint()) < 0)
        {
          // Existing entry already covers this; we don't have to do anything more
          return;
        }
        underlying.remove(prior.getKey());
        // Set our start to the start of the prior entry
        resultantStart = prior.getKey();
      }
      else
      {
        // Values are different; truncate prior item
        underlying.put(prior.getKey(), new TwoTuple<>(Range.closedOpen(prior.getKey(), resultantStart), prior.getValue().getT()));
        // If the prior entry stretches beyond the new entry we also need to put in our remaining item
        if (resultantEnd.compareTo(prior.getValue().getS().upperEndpoint()) < 0)
        {
          underlying.put(resultantEnd, new TwoTuple<>(Range.closedOpen(resultantEnd, prior.getValue().getS().upperEndpoint()), prior.getValue().getT()));
        }

      }
    }

    // Remove any items which are covered by our new entry, and truncate or coalesce anything which overlaps the end of it
    Map.Entry<K, TwoTuple<Range<K>, V>> potentialVictim = underlying.ceilingEntry(resultantStart);
    while (potentialVictim != null)
    {
      if (key.encloses(potentialVictim.getValue().getS()))
      {
        // Totally enclosed; remove it
        underlying.remove(potentialVictim.getKey());
        potentialVictim = underlying.ceilingEntry(resultantStart);
      }
      else if (key.contains(potentialVictim.getKey()))
      {
        // Partial overlap
        if (potentialVictim.getValue().getT().equals(value))
        {
          // Values are the same so we can coalesce.  Remove the entry and update our bounds accordingly
          resultantEnd = potentialVictim.getValue().getS().upperEndpoint();
          underlying.remove(potentialVictim.getKey());
        }
        else
        {
          // Values are different; truncate victim item
          underlying.remove(potentialVictim.getKey());
          underlying.put(resultantEnd, new TwoTuple<>(Range.closedOpen(resultantEnd, potentialVictim.getValue().getS().upperEndpoint()), potentialVictim.getValue().getT()));
        }
        potentialVictim = null;
      }
      else
      {
        // No relationship
        potentialVictim = null;
      }
    }

    // Write out our final result
    underlying.put(resultantStart, new TwoTuple<>(Range.closedOpen(resultantStart, resultantEnd), value));
  }

  @Nullable
  private K findFloor(final Range<K> key)
  {
    final K lowerEndpoint;
    final Map.Entry<K, TwoTuple<Range<K>, V>> floorEntry = underlying.floorEntry(key.lowerEndpoint());

    if (floorEntry != null && floorEntry.getValue().getS().upperEndpoint().compareTo(key.lowerEndpoint()) > 0)
    {
      lowerEndpoint = floorEntry.getKey();
    }
    else
    {
      lowerEndpoint = key.lowerEndpoint();
    }
    return lowerEndpoint;
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

  @Override
  public String toString()
  {
    final Objects.ToStringHelper sh = Objects.toStringHelper(this);

    final NavigableSet<K> ns = underlying.navigableKeySet();
    for (final K key : ns)
    {
      sh.addValue(underlying.get(key));
    }

    return sh.toString();
  }
}
