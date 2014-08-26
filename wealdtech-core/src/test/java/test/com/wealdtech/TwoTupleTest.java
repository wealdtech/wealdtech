/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech;

import org.testng.annotations.Test;

import com.wealdtech.TwoTuple;

import static org.testng.Assert.*;

public class TwoTupleTest
{
  @Test
  public void testClass() throws Exception
  {
    final TwoTuple<String, Integer> testTwoTuple1 = new TwoTuple<>("Test", 1);
    testTwoTuple1.getS();
    testTwoTuple1.getT();
    testTwoTuple1.toString();
    testTwoTuple1.hashCode();
    assertEquals(testTwoTuple1, testTwoTuple1);
    assertNotEquals(testTwoTuple1, null);
    assertNotEquals(null, testTwoTuple1);
    assertFalse(testTwoTuple1.equals("test"));

    final TwoTuple<String, Integer> testTwoTuple2 = new TwoTuple<>("Another test", 2);
    testTwoTuple2.getS();
    testTwoTuple2.getT();
    assertNotEquals(testTwoTuple1, testTwoTuple2);

    final TwoTuple<String, Integer> testTwoTuple3 = new TwoTuple<>(null, null);
    testTwoTuple3.getS();
    testTwoTuple3.getT();
    testTwoTuple1.toString();
    testTwoTuple1.hashCode();
    assertEquals(testTwoTuple3, testTwoTuple3);
    assertNotEquals(testTwoTuple1, testTwoTuple3);

    final TwoTuple<String, Integer> testTwoTuple4 = new TwoTuple<>("Test", 2);
    assertNotEquals(testTwoTuple1, testTwoTuple4);
  }
}

