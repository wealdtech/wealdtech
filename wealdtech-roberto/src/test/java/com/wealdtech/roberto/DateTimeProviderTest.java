/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto;

import com.google.common.base.Optional;
import com.wealdtech.roberto.dataprovider.DateTimeDataProvider;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test of datetime data provider
 */
public class DateTimeProviderTest
{
  @Test
  public void simpleTest()
  {
    final DateTimeDataProvider provider = new DateTimeDataProvider();
    provider.startProviding();
    provider.fetch();
    final Optional<DateTime> dt = provider.get();
    assertTrue(dt.isPresent());
  }

  @Test
  public void repeatedTest()
  {
    final DateTimeDataProvider provider = new DateTimeDataProvider();
    provider.startProviding();
    provider.fetch();
    final Optional<DateTime> dt = provider.get();
    assertTrue(dt.isPresent());
    try { Thread.sleep(2000L); }
    catch (final InterruptedException ie)
    {
      fail("Interrupted");
    }
    final Optional<DateTime> dt2 = provider.get();
    assertTrue(dt2.isPresent());
    assertTrue(dt2.get().isAfter(dt.get()));
  }
}
