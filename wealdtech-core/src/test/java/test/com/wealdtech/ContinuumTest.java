/*
 * Copyright 2013 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech;

import com.wealdtech.Continuum;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class ContinuumTest
{
  @Test
  public void testFromInt() throws Exception
  {
    final Continuum cnt = Continuum.fromInt(220);
    assertEquals(cnt.getElement(), 2);
    assertEquals(cnt.getLevel(), -30);
  }

  @Test
  public void testIncrement() throws Exception
  {
    Continuum cnt = Continuum.fromInt(199);
    assertEquals(cnt.getElement(), 1);
    assertEquals(cnt.getLevel(), 49);

    cnt = cnt.increment();
    assertEquals(cnt.getElement(), 2);
    assertEquals(cnt.getLevel(), -50);

    cnt = cnt.decrement();
    assertEquals(cnt.getElement(), 1);
    assertEquals(cnt.getLevel(), 49);
  }

  @Test
  public void testDecrement() throws Exception
  {
    Continuum cnt = Continuum.fromInt(200);
    assertEquals(cnt.getElement(), 2);
    assertEquals(cnt.getLevel(), -50);

    cnt = cnt.decrement();
    assertEquals(cnt.getElement(), 1);
    assertEquals(cnt.getLevel(), 49);
  }

  @Test
  public void testToString() throws Exception
  {
    final Continuum cnt = Continuum.fromInt(103);
    assertEquals(cnt.toString(), "Two (-47)");
  }
}

