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
import com.wealdtech.collect.RangedMultimap;
import com.wealdtech.collect.TreeRangedMultimap;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 */
public class TreeRangedMultimapTest
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
    final RangedMultimap<Integer, String> map = new TreeRangedMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 5);
    map.put(testRange1, "Test1");
    final Range<Integer> testRange2 = Range.closedOpen(7, 10);
    map.put(testRange2, "Test2");

    final Collection<String> items = map.get(Range.closedOpen(4, 8));
    assertEquals(items.size(), 2);
    assertContains(items, "Test1");
    assertContains(items, "Test2");
  }

  // Ensure that touching but not overlapping items are obtained correctly
  @Test
  public void testTouching()
  {
    final RangedMultimap<Integer, String> map = new TreeRangedMultimap<>();

    final Range<Integer> testRange1 = Range.closedOpen(0, 10);
    map.put(testRange1, "Test1");
    final Range<Integer> testRange2 = Range.closedOpen(10, 20);
    map.put(testRange2, "Test2");

    final Collection<String> items1 = map.get(Range.closedOpen(0, 10));
    assertEquals(items1.size(), 1);
    assertContains(items1, "Test1");

    final Collection<String> items2 = map.get(Range.closedOpen(10, 20));
    assertEquals(items2.size(), 1);
    assertContains(items2, "Test2");
  }
}
