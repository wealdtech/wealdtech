/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

/**
 * Configuration for a Guava cache.
 */
public class CacheConfiguration
{
  private final int maxEntries;
  private static final int MAXENTRIES_DEFAULT = 10000;

  private final int maxDuration;
  private static final int MAXDURATION_DEFAULT = 120;

  @Inject
  public CacheConfiguration()
  {
    this(null, null);
  }

  @JsonCreator
  private CacheConfiguration(@JsonProperty("maxsize") final Integer maxSize,
                             @JsonProperty("maxduration") final Integer maxDuration)
  {
    if (maxSize == null)
    {
      this.maxEntries = MAXENTRIES_DEFAULT;
    }
    else
    {
      this.maxEntries = maxSize;
    }
    if (maxDuration == null)
    {
      this.maxDuration = MAXDURATION_DEFAULT;
    }
    else
    {
      this.maxDuration = maxDuration;
    }
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
