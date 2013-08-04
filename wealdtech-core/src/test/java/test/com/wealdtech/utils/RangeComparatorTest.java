/*
 * Copyright 2013 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.utils;

import com.google.common.collect.Range;
import com.wealdtech.utils.RangeComparator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class RangeComparatorTest
{

  @Test
  public void testNoLowerBound1() throws Exception
  {
    final Range<Integer> lower = Range.lessThan(2);
    final Range<Integer> upper = Range.lessThan(3);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testNoLowerBound2() throws Exception
  {
    final Range<Integer> lower = Range.lessThan(2);
    final Range<Integer> upper = Range.closedOpen(2, 3);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testClosedLowerBound1() throws Exception
  {
    final Range<Integer> lower = Range.closedOpen(2, 5);
    final Range<Integer> upper = Range.closedOpen(3, 5);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testClosedLowerBound2() throws Exception
  {
    final Range<Integer> lower = Range.open(2, 5);
    final Range<Integer> upper = Range.closedOpen(2, 5);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testNoUpperBound1() throws Exception
  {
    final Range<Integer> lower = Range.greaterThan(2);
    final Range<Integer> upper = Range.greaterThan(3);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testNoUpperBound2() throws Exception
  {
    final Range<Integer> lower = Range.greaterThan(2);
    final Range<Integer> upper = Range.closedOpen(2, 3);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testClosedUpperBound1() throws Exception
  {
    final Range<Integer> lower = Range.openClosed(2, 5);
    final Range<Integer> upper = Range.openClosed(2, 6);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

  @Test
  public void testClosedUpperBound2() throws Exception
  {
    final Range<Integer> lower = Range.open(2, 5);
    final Range<Integer> upper = Range.openClosed(2, 5);
    assertTrue(new RangeComparator<Integer>().compare(lower, upper) < 0);
  }

}
