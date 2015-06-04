/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class HashTest
{
  @Test
  public void testCache() throws Exception
  {
    // Ensure that the cache is operational
    final String fredHash = Crypt.hash("Fred");
    // call to initialise the Hash class information
    Hash.matches("x", "x");
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
