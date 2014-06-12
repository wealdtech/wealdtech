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
}
