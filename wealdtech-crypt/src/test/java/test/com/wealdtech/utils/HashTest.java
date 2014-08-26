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

import org.testng.annotations.Test;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.wealdtech.DataError;
import com.wealdtech.utils.Hash;
import com.wealdtech.utils.WealdMetrics;

import static org.testng.Assert.*;

public class HashTest
{
  @Test
  public void testHash() throws Exception
  {
    final String hashed = Hash.hash("Test");
    assertNotNull(hashed);
    assertTrue(Hash.matches("Test", hashed));
  }

  @Test
  public void testNullInput() throws Exception
  {
    try
    {
      Hash.hash(null);
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
    final String hashed = Hash.hash("Test");
    assertTrue(Hash.isHashed(hashed));
    assertFalse(Hash.isHashed("Test"));
    try
    {
      Hash.isHashed(null);
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
    final String hashed = Hash.hash("Test");
    assertFalse(Hash.matches(null, hashed));
  }

  @Test
  public void testNullHashed() throws Exception
  {
    try
    {
      assertFalse(Hash.matches("Test", null));
      fail("Attempted to match against NULL hashed value");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }

  @Test
  public void testCache() throws Exception
  {
    // Ensure that the cache is operational
    final String fredHash = Hash.hash("Fred");
    final MetricRegistry registry = WealdMetrics.getMetricRegistry();
    final Meter lookups = registry.getMeters().get(com.codahale.metrics.MetricRegistry.name(Hash.class, "lookups"));
    long initialLookupCount = lookups.getCount();
    final Meter misses = registry.getMeters().get(com.codahale.metrics.MetricRegistry.name(Hash.class, "misses"));
    long initialMissCount = misses.getCount();
    Hash.matches("Fred", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 1);
    assertEquals(misses.getCount() - initialMissCount, 1);
    Hash.matches("Fred", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 2);
    assertEquals(misses.getCount() - initialMissCount, 1);
    Hash.matches("Joe", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 3);
    assertEquals(misses.getCount() - initialMissCount, 2);
  }
}
