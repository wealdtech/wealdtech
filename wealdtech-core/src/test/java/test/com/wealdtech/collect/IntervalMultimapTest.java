/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.collect;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.wealdtech.collect.IntervalMultimap;
import com.wealdtech.collect.RangedMultimap;
import com.wealdtech.collect.TreeRangedMultimap;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * Tests for interval tree
 */
public class IntervalMultimapTest
{
  private void assertContains(final Collection<String> collection, final String val)
  {
    assertNotNull(collection, "Collection expected to be present");
    assertTrue(collection.contains(val), "Collection missing expected value \"" + val + "\"");
  }

  // Ensure that a ranged get works
  @Test
  public void testRangedGet()
  {
    final IntervalMultimap<Integer, String> map = new IntervalMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 5);
    map.put(testRange1, "Test 1");
    final Range<Integer> testRange2 = Range.closedOpen(7, 10);
    map.put(testRange2, "Test 2");

    final Collection<String> result = map.get(Range.closedOpen(4, 8));
    assertEquals(result.size(), 2);
    assertTrue(result.contains("Test 1"));
    assertTrue(result.contains("Test 2"));
  }

  // Ensure that a get inside the range works
  @Test
  public void InternalGet()
  {
    final IntervalMultimap<Integer, String> map = new IntervalMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 10);
    map.put(testRange1, "Test 1");
    final Range<Integer> testRange2 = Range.closedOpen(2, 10);
    map.put(testRange2, "Test 2");

    final Collection<String> result = map.get(Range.closedOpen(5, 6));
    assertEquals(result.size(), 2);
    assertTrue(result.contains("Test 1"));
    assertTrue(result.contains("Test 2"));
  }

  // Ensure that a get with stacked ranges works
  @Test
  public void StackedGet()
  {
    final IntervalMultimap<Integer, String> map = new IntervalMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 10);
    map.put(testRange1, "Test 1");
    final Range<Integer> testRange2 = Range.closedOpen(2, 11);
    map.put(testRange2, "Test 2");
    final Range<Integer> testRange3 = Range.closedOpen(3, 12);
    map.put(testRange3, "Test 3");
    final Range<Integer> testRange4 = Range.closedOpen(4, 13);
    map.put(testRange4, "Test 4");
    final Range<Integer> testRange5 = Range.closedOpen(5, 14);
    map.put(testRange5, "Test 5");

    final Collection<String> result = map.get(Range.closedOpen(3, 4));
    assertEquals(result.size(), 3);
    assertTrue(result.contains("Test 1"));
    assertTrue(result.contains("Test 2"));
    assertTrue(result.contains("Test 3"));
  }

  // Ensure that touching but not overlapping items are obtained correctly
  @Test
  public void testTouching()
  {
    final IntervalMultimap<Integer, String> map = new IntervalMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(0, 10);
    map.put(testRange1, "Test 1");
    final Range<Integer> testRange2 = Range.closedOpen(10, 20);
    map.put(testRange2, "Test 2");

    final Collection<String> result1 = map.get(Range.closedOpen(0, 10));
    assertEquals(result1.size(), 1);
    assertTrue(result1.contains("Test 1"));

    final Collection<String> result2 = map.get(Range.closedOpen(10, 20));
    assertEquals(result2.size(), 1);
    assertTrue(result2.contains("Test 2"));
  }
}
