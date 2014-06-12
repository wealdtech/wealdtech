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
 * A mapping from disjoint nonempty ranges to non-null values.  Queries
 */
public interface RangedMap<K extends Comparable, V>
{

  /**
   * Returns the value associated with the specified key, or {@code null} if there is no such value.
   *
   * @param key
   * @return
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
   * @param key
   * @return
   */
  @Nullable
  Map.Entry<Range<K>, V> getEntry(K key);

  void put(Range<K> key, V value);

  void putAll(RangedMap<K, V> rangedMap);

  void clear();

  void remove(K key);

  void remove(Range<K> range);
}
