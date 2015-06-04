/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.utils;

import com.wealdtech.DataError;
import com.wealdtech.utils.Crypt;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HashTest
{
  @Test
  public void testHash() throws Exception
  {
    final String hashed = Crypt.hash("Test");
    assertNotNull(hashed);
    assertTrue(Crypt.matches("Test", hashed));
  }

  @Test
  public void testNullInput() throws Exception
  {
    try
    {
      Crypt.hash(null);
      fail("Hashed NULL");
    }
    catch (DataError.Missing de)
    {
      // Good
    }

  }

  @Test
  public void testIsHashed() throws Exception
  {
    final String hashed = Crypt.hash("Test");
    assertTrue(Crypt.isHashed(hashed));
    assertFalse(Crypt.isHashed("Test"));
    try
    {
      Crypt.isHashed(null);
      fail("Considered NULL for hashed");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }

  @Test
  public void testNullMatch() throws Exception
  {
    final String hashed = Crypt.hash("Test");
    assertFalse(Crypt.matches(null, hashed));
  }

  @Test
  public void testNullHashed() throws Exception
  {
    try
    {
      assertFalse(Crypt.matches("Test", null));
      fail("Attempted to match against NULL hashed value");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }
}
