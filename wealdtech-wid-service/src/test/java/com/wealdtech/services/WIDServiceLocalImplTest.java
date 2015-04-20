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
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  // Ensure that multithreaded obtains do not return duplicate IDs
  @Test
  public void testUniqueMultithreaded()
  {
    ConcurrentHashMap<WID<?>, Boolean> map = new ConcurrentHashMap<>();
    final Set<WID<?>> set = Collections.newSetFromMap(map);

    final ExecutorService executor = Executors.newFixedThreadPool(32);
    for (int i = 0; i < 32; i++) {

      Runnable worker = new Runnable(){
        @Override
        public void run()
        {
          for (int j = 0; j <1024; j++)
          {
            map.put(service.obtain(), true);
          }
        }
      };
      executor.execute(worker);
    }
    executor.shutdown();
    while (!executor.isTerminated()) {}

    assertEquals(set.size(), 1024*32, "Incorrect number of WIDs obtained: " + set.size());
  }
}
