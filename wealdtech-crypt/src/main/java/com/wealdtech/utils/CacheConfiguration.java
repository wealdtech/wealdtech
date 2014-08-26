/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

import static com.wealdtech.Preconditions.*;

/**
 * Configuration for a Guava cache.
 */
public class CacheConfiguration implements Configuration
{
  /**
   * The maximum number of entries in the cache
   */
  private int maxEntries = 10000;

  /**
   * The maximum number of seconds an entry will remain in the cache
   */
  private int maxDuration = 120;

  @Inject
  public CacheConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private CacheConfiguration(@JsonProperty("maxentries") final Integer maxEntries,
                             @JsonProperty("maxduration") final Integer maxDuration)
  {
    this.maxEntries = Objects.firstNonNull(maxEntries, this.maxEntries);
    this.maxDuration = Objects.firstNonNull(maxDuration, this.maxDuration);
    validate();
  }

  private void validate()
  {
    checkNotNull(this.maxEntries, "Maximum size is required");
    checkState(this.maxEntries >= 0 && this.maxEntries <= 1000000, "Maximum size must be between 0 and 1,000,000");
    checkNotNull(this.maxDuration, "Maximum size is required");
    checkState(this.maxDuration >= 0 && this.maxDuration <= 86400, "Maximum duration must be between 0 and 86,400");
  }

  /**
   * Obtain the maximum number of entries in the cache.
   * @return The maximum number of entries
   */
  public int getMaxEntries()
  {
    return this.maxEntries;
  }

  /**
   * Obtain the maximum amount of time an entry will
   * stay in the cache, in seconds.
   * @return The maximum number of seconds an entry will
   * remain in the cache.
   */
  public int getMaxDuration()
  {
    return this.maxDuration;
  }

  public static class Builder
  {
    private Integer maxEntries;
    private Integer maxDuration;

    /**
     * Start to build a Guava cache configuration.
     */
    public Builder()
    {
      // Nothing to do
    }

    /**
     * Start to build a Guava cache configuration based on a prior configuration.
     * @param prior the prior configuration.
     */
    public Builder(final CacheConfiguration prior)
    {
      this.maxEntries = prior.maxEntries;
      this.maxDuration = prior.maxDuration;
    }

    public Builder maxEntries(final Integer maxEntries)
    {
      this.maxEntries = maxEntries;
      return this;
    }

    public Builder maxDuration(final Integer maxDuration)
    {
      this.maxDuration = maxDuration;
      return this;
    }

    public CacheConfiguration build()
    {
      return new CacheConfiguration(this.maxEntries, this.maxDuration);
    }
  }
}
