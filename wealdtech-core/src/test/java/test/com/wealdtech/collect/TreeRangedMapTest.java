/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.collect;

import com.google.common.collect.Range;
import com.wealdtech.collect.RangedMap;
import com.wealdtech.collect.TreeRangedMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 */
public class TreeRangedMapTest
{
  // Ensure that a simple get works
  @Test
  public void testSimpleGet()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange = Range.closedOpen(1, 5);
    map.put(testRange, "Test");

    assertEquals(map.get(3), "Test");
    assertNull((map.get(5)));
  }

  // Test that overlapping puts work
  @Test
  public void testOverlappingPuts()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(2, 5);
    map.put(testRange1, "Test1");
    assertNull(map.get(1));
    assertEquals(map.get(2), "Test1");
    assertEquals(map.get(3), "Test1");
    assertEquals(map.get(4), "Test1");
    assertNull(map.get(5));

    final Range<Integer> testRange2 = Range.closedOpen(3, 6);
    map.put(testRange2, "Test2");
    assertNull(map.get(1));
    assertEquals(map.get(2), "Test1");
    assertEquals(map.get(3), "Test2");
    assertEquals(map.get(4), "Test2");
    assertEquals(map.get(5), "Test2");
    assertNull(map.get(6));

    final Range<Integer> testRange3 = Range.closedOpen(1, 2);
    map.put(testRange3, "Test3");
    assertEquals(map.get(1), "Test3");
    assertEquals(map.get(2), "Test1");
    assertEquals(map.get(3), "Test2");
    assertEquals(map.get(4), "Test2");
    assertEquals(map.get(5), "Test2");
    assertNull(map.get(6));
  }

  // Test coalescing ranges
  @Test
  public void testCoalescingPuts1()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(2, 5);
    map.put(testRange1, "Test");
    assertEquals(map.size(), 1);

    final Range<Integer> testRange2 = Range.closedOpen(6, 8);
    map.put(testRange2, "Test");
    assertEquals(map.size(), 2);

    final Range<Integer> testRange3 = Range.closedOpen(4, 7);
    map.put(testRange3, "Test");
    assertEquals(map.size(), 1);
  }

  // Test coalescing ranges when new entry is already catered for in old entry
  @Test
  public void testCoalescingPuts2()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 8);
    map.put(testRange1, "Test");
    System.err.println(map);
    assertEquals(map.size(), 1);

    final Range<Integer> testRange2 = Range.closedOpen(2, 5);
    map.put(testRange2, "Test");
    System.err.println(map);
    assertEquals(map.size(), 1);
  }

  // Ensure that putting a new value in the middle of an existing value breaks it appropriately
  @Test
  public void testSplitEntry()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 5);
    map.put(testRange1, "Test1");
    assertEquals(map.size(), 1);

    final Range<Integer> testRange2 = Range.closedOpen(3, 4);
    map.put(testRange2, "Test2");
    assertEquals(map.size(), 3);
    assertEquals(map.get(1), "Test1");
    assertEquals(map.get(2), "Test1");
    assertEquals(map.get(3), "Test2");
    assertEquals(map.get(4), "Test1");
  }


  // Ensure that zero-length ranges are not allowed
  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testZeroLengthRanges()
  {
    final RangedMap<Integer, String> map = new TreeRangedMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(2, 2);
    map.put(testRange1, "Test1");
  }
}
