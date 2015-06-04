/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.wealdtech.DataError;
import com.wealdtech.WID;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a WID service
 */
public class WIDConfiguration implements Configuration
{
  private final Long shardId;

  @JsonCreator
  public WIDConfiguration(@JsonProperty("shardid") final Long shardId)
  {
    this.shardId = MoreObjects.firstNonNull(shardId, 0L);
    if (shardId < 0 || shardId > WID.MAX_SHARD)
    {
      throw new DataError.Bad("Invalid shard ID");
    }
  }

  /**
   * @return the shard ID for generated WIDs
   */
  public Long getShardId()
  {
    return this.shardId;
  }
}
