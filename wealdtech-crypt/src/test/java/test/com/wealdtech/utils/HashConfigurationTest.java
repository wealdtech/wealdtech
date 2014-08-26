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

import com.wealdtech.DataError;
import com.wealdtech.utils.CacheConfiguration;
import com.wealdtech.utils.HashConfiguration;

import static org.testng.Assert.*;

public class HashConfigurationTest
{
  @Test
  public void testHashConfiguration() throws Exception
  {
    HashConfiguration testHashConfiguration1 = new HashConfiguration.Builder()
                                                                    .strength(10)
                                                                    .cacheConfiguration(new CacheConfiguration())
                                                                    .build();

    HashConfiguration testHashConfiguration2 = new HashConfiguration.Builder(testHashConfiguration1)
                                                                    .strength(5)
                                                                    .cacheConfiguration(new CacheConfiguration())
                                                                    .build();

    assertNotEquals(testHashConfiguration1, testHashConfiguration2);
  }

  @Test
  public void testInvalidHashConfiguration() throws Exception
  {
    try
    {
      new HashConfiguration.Builder().strength(2).build();
      fail("Created hash configuration with invalid strength");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }
}
