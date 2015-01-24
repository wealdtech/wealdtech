/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.collect.Sets;
import com.wealdtech.WID;
import com.wealdtech.config.WIDConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

/**
 */
public class WIDServiceLocalImplTest
{
  private static final long SHARD_ID = 123l;

  private WIDService service;

  @BeforeClass
  public void setUp()
  {
    this.service = new WIDServiceLocalImpl(new WIDConfiguration(SHARD_ID));
  }

  // Ensure that the configured shard ID is used in WID generation
  @Test
  public void testShardId()
  {
    WID<Object> id = service.obtain();
    assertEquals(id.getShardId(), SHARD_ID);
  }

  // Ensure that we cannot obtain duplicate shard IDs by looping
  @Test
  public void testUnique()
  {
    final ArrayList<WID<?>> ids = new ArrayList<>(10000);
    for (int i = 0; i < 10000; i++)
    {
      ids.add(service.obtain());
    }
    assertEquals(Sets.newHashSet(ids).size(), ids.size());
  }
}
