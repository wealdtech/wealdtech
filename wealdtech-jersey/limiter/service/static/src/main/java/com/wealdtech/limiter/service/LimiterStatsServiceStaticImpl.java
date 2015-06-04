/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.limiter.service;

import com.google.common.collect.Maps;
import com.wealdtech.limiter.LimiterStats;
import com.wealdtech.limiter.services.LimiterStatsService;

import java.util.Map;

/**
 * Simple static rate limiter using in-memory map
 */
public class LimiterStatsServiceStaticImpl implements LimiterStatsService
{
  private Map<String, LimiterStats> map = Maps.newHashMap();

  @Override
  public LimiterStats obtain(final Long timestamp, final String key)
  {
    LimiterStats stats = map.get(key);
    if (stats == null || stats.getTimestamp() < timestamp)
    {
      // New or outdated
      stats = LimiterStats.builder().timestamp(timestamp).key(key).requests(0l).build();
      map.put(key, stats);
    }
    return stats;
  }

  @Override
  public void update(final LimiterStats stats)
  {
    map.put(stats.getKey(), stats);
  }
}
