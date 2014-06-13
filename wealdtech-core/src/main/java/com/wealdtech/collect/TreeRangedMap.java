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
    return underlying.floorEntry(key);
  }

  private K findFirstOverlappingKey(final K key)
  {
    return null;
  }

  @Override
  public void put(final Range<K> key, final V value)
  {
    TwoTuple<Range<K>, V> currentTuple = new TwoTuple<>(key, value);
    K overlapKey = findFirstOverlappingKey(key.lowerEndpoint());
    while (currentTuple != null)
    {
      final TwoTuple<?, ?> result = overlay(getEntry(currentTuple.getS().lowerEndpoint()).getValue(), new TwoTuple(currentTuple.getS(), value));

    }
    if (currentTuple != null)
    {
      overlapKey = findFirstOverlappingKey(currentTuple.getS().lowerEndpoint());
      underlying.put(currentTuple.getS().lowerEndpoint(), currentTuple);
    }
  }
//    // Find any overlapping ranges.  Because we base our key on the start of the range we might need to see if our start is in the
//    // middle of an existing range
//    System.err.println("findFloor() returns " + findFloor(key));
//    final NavigableMap<K, TwoTuple<Range<K>, V>> overlapMap = underlying.subMap(findFloor(key), true, key.upperEndpoint(), true);
//
//    if (overlapMap.isEmpty())
//    {
//      // Simple case; just insert it
//      System.err.println("No overlap");
//      underlying.put(key.lowerEndpoint(), new TwoTuple<>(key, value));
//    }
//    else
//    {
//
//      System.err.println("Overlap is " + overlapMap);
//      // There is some overlap; we might need to splice the entries.
//      final Collection<K> toRemove = Sets.newHashSet();
//      final Map<K, TwoTuple<Range<K>, V>> toAdd = Maps.newHashMap();
//
//      K lower = key.lowerEndpoint();
//      K upper = key.upperEndpoint();
//      // Work through the overlap, altering it as required so that the new entry overrides it
//      for (final K overlapKey : overlapMap.navigableKeySet())
//      {
//        final TwoTuple<Range<K>, V> overlapValue = underlying.get(overlapKey);
//        final Range<K> overlapRange = overlapValue.getS();
//
//        if (overlapRange.lowerEndpoint().compareTo(lower) < 0)
//        {
//          // The current overlap range starts before the range we're putting.
//          if (value.equals(overlapValue.getT()))
//          {
//            // Values are the same; just push our boundary up
//            lower = overlapRange.upperEndpoint();
//          }
//          else
//          {
//            // Values are different; splice the item
//            toRemove.add(overlapRange.lowerEndpoint());
//            toAdd.put(lower, new TwoTuple<>(Range.closedOpen(overlapRange.lowerEndpoint(), lower), overlapValue.getT()));
//          }
//        }
//        else if (overlapRange.lowerEndpoint().equals(lower))
//        {
//          // The current overlap range starts at the same time as the range we're putting
//          if (value.equals(overlapValue.getT()))
//          {
//            // Values are the same; just push our boundary up
//            lower = overlapRange.upperEndpoint();
//          }
//          else
//          {
//            // Values are different; splice the item
//            toRemove.add(overlapRange.lowerEndpoint());
//            toAdd.put(lower, new TwoTuple<>(Range.closedOpen(overlapRange.lowerEndpoint(), lower), overlapValue.getT()));
//          }
//        }
//        // Carry out our replacements
//        for (final K toRemoveKey : toRemove)
//        {
//          underlying.remove(toRemoveKey);
//        }
//        underlying.putAll(toAdd);
//      }
//    }
//  }

  /**
   * Overlay one entry on top of another.  This results in somewhere between 1 and 3 entries
   * @param current the current entry
   * @param overlay the entry to overlay
   * @return the remaining overlay (the rightmost piece of the overlay beyond the current range)
   */
  private TwoTuple<Range<K>, V> overlay(final TwoTuple<Range<K>, V> current, final TwoTuple<Range<K>, V> overlay)
  {
    remove(current.getS().lowerEndpoint());
//    final Map<K, TwoTuple<Range<K>, V>> results = Maps.newHashMap();

    // Temp. variables for simplification of ensuing code
    final K currentStart = current.getS().lowerEndpoint();
    final K currentEnd = current.getS().upperEndpoint();
    final K overlayStart = overlay.getS().lowerEndpoint();
    final K overlayEnd = overlay.getS().upperEndpoint();

    if (currentStart.compareTo(overlayStart) > 0 && currentEnd.compareTo(overlayEnd) < 0)
    {
      // The overlay covers the entire current; simple replace
      return overlay;
//      results.put(overlayStart, overlay);
    }
    else
    {
      if (currentStart.compareTo(overlayStart) > 0)
      {
        // The current entry starts before the overlay; add partial current
        put(Range.closedOpen(currentStart, overlayStart), current.getT());
//        results.put(currentStart, new TwoTuple<>(Range.closedOpen(currentStart, overlayStart), current.getT()));
      }
      if (currentEnd.compareTo(overlayEnd) < 0)
      {
        // The current entry ends after the overlay; add partial current
        put(Range.closedOpen(overlayEnd, currentEnd), current.getT());
//        results.put(currentStart, new TwoTuple<>(Range.closedOpen(overlayEnd, currentEnd), current.getT()));
      }
      // The remainder is the overlay
//      results.put(currentStart, new TwoTuple<>(Range.closedOpen(overlayStart, overlayEnd), overlay.getT()));
    }
    return new TwoTuple<>(Range.closedOpen(overlayStart, overlayEnd), overlay.getT());
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
