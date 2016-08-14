/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.collect;

import com.google.common.collect.Range;
import com.wealdtech.collect.IntervalMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests for interval map
 */
public class IntervalMapTest
{
    // Ensure that a simple get works
    @Test
    public void testGet()
    {
      final IntervalMap<Integer, String> map = new IntervalMap<>();

      final Range<Integer> testRange1 = Range.closedOpen(1, 5);
      map.put(testRange1, "Test 1");

      final String result0 = map.get(0);
      assertNull(result0);
      final String result1 = map.get(1);
      assertEquals(result1, "Test 1");
      final String result5 = map.get(5);
      assertNull(result5);
    }

  // Ensure that a get of touching ranges works
  @Test
  public void testTouching()
  {
    final IntervalMap<Integer, String> map = new IntervalMap<>();

    final Range<Integer> testRange1 = Range.closedOpen(1, 5);
    map.put(testRange1, "Test 1");
    final Range<Integer> testRange2 = Range.closedOpen(5, 10);
    map.put(testRange2, "Test 2");

    final String result1 = map.get(4);
    assertEquals(result1, "Test 1");
    final String result5 = map.get(5);
    assertEquals(result5, "Test 2");
  }
}
