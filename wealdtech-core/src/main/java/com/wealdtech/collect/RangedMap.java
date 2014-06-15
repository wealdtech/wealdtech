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
import com.wealdtech.TwoTuple;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * A mapping from disjoint nonempty ranges to non-null values.  A single point will only ever have a maximum of one value, with
 * later values taking precedence over earlier values.
 * <p/>
 * Values can be obtained for a single point or for a range of points.
 */
public interface RangedMap<K extends Comparable, V>
{

  /**
   * Returns the value associated with the specified key, or {@code null} if there is no such value.
   *
   * @param key the key
   * @return the value; can be {@code null}
   */
  @Nullable
  V get(K key);

  /**
   * Returns all values associated with the specified range.
   *
   * @param range the range over which to obtain the values
   *
   * @return a list containing two tuples of the range and value, ordered by range.  Note that the first returned range might start
   * before the provided range and the last returned range might finish after the provided range
   */
  List<TwoTuple<Range<K>, V>> get(Range<K> range);

  /**
   * returns the entry for the given key
   * @param key the key
   * @return the entry; can be {@code null}
   */
  @Nullable
  Map.Entry<K, TwoTuple<Range<K>, V>> getEntry(K key);

  /**
   * Add a value to the map.  Any existing values in the given range will be overwritten
   * @param key the range over which the value applies
   * @param value the value
   */
  void put(Range<K> key, V value);

  /**
   * Add multiple values to the map.  Any existing values in the given range will be overwritten
   * @param rangedMap an existing ranged map
   */
  void putAll(RangedMap<K, V> rangedMap);

  /**
   * Remove all entries from this map
   */
  void clear();

  void remove(K key);

  void remove(Range<K> range);

  int size();
}
