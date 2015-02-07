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

import com.google.inject.Inject;
import com.wealdtech.WID;
import com.wealdtech.config.WIDConfiguration;

/**
 * Service to provide locally generated WIDs
 */
public class WIDServiceLocalImpl implements WIDService
{
  private final WIDConfiguration configuration;

  // The next value of the ID fragment
  private int nextId;

  // The value of the timestamp the last time we generated a WID
  private long thisMs;

  // The first value of the ID fragment we generated this millisecond
  private int thisMsFirstId;

  @Inject
  public WIDServiceLocalImpl(final WIDConfiguration configuration)
  {
    this.configuration = configuration;

    // Initialise our state
    thisMs = System.currentTimeMillis();
    thisMsFirstId = 0;
    nextId = 0;
  }

  public <T> WID<T> obtain()
  {
    nextId = (int)((nextId + 1) % (WID.MAX_IID + 1));
    long ms = System.currentTimeMillis();
    if (ms != thisMs)
    {
      // A new millisecond since we last obtained an ID
      thisMs = ms;
      thisMsFirstId = nextId;
    }
    else
    {
      if (nextId == thisMsFirstId)
      {
        // We've hit the limit of the number of IDs we can generate this millisecond.  Busy wait until the next ms ticks over
        do
        {
          ms = System.currentTimeMillis();
        }
        while (ms == thisMs);
        thisMs = ms;
        thisMsFirstId = nextId;
      }
    }
    return WID.fromComponents(configuration.getShardId(), thisMs, nextId);
  }
}
