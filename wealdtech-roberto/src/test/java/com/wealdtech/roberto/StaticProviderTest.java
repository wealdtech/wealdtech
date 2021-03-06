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
import com.wealdtech.roberto.dataprovider.StaticDataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test of static data provider
 */
public class StaticProviderTest
{
  @Test
  public void stringTest()
  {
    final DataProvider<String> stringProvider = new StaticDataProvider<>("Hello, world");
    final Optional<String> string = stringProvider.get();
    assertTrue(string.isPresent());
    assertEquals(string.get(), "Hello, world");
  }
}
