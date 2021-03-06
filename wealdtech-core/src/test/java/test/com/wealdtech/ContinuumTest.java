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

import static org.testng.Assert.assertTrue;


public class ContinuumTest
{
  @Test
  public void testComparison() throws Exception
  {
    final IntegerContinuum c1 = new IntegerContinuum(150);
    final IntegerContinuum c2 = new IntegerContinuum(151);
    assertTrue(c1.compareTo(c2) < 0);
  }
}

