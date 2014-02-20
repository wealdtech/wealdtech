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
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

import static com.wealdtech.Preconditions.*;

/**
 * Configuration for the hash utility.
 */
public class HashConfiguration implements Configuration
{
  /**
   * The configuration of the cache of local hashes
   */
  private CacheConfiguration cacheConfiguration = new CacheConfiguration();

  /**
   * The strength of the bcrypt algorithm.  Each increment implies a doubling
   * of the time taken to generate the hash
   */
  private int strength = 12;

  @Inject
  public HashConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private HashConfiguration(@JsonProperty("cache") final CacheConfiguration cacheConfiguration,
                            @JsonProperty("strength") final Integer strength)
  {
    this.cacheConfiguration = Objects.firstNonNull(cacheConfiguration, this.cacheConfiguration);
    this.strength = Objects.firstNonNull(strength, this.strength);
    validate();
  }

  private void validate()
  {
    checkNotNull(this.cacheConfiguration, "Cache configuration is required");
    checkNotNull(this.strength, "Strength is required");
    checkState(this.strength >= 4 && this.strength <= 20, "Strength must be between 4 and 20");
  }

  public CacheConfiguration getCacheConfiguration()
  {
    return this.cacheConfiguration;
  }

  public int getStrength()
  {
    return this.strength;
  }

  public static class Builder
  {
    private CacheConfiguration cacheConfiguration;
    private Integer strength;

    /**
     * Start to build a hash configuration.
     */
    public Builder()
    {
      // Nothing to do
    }

    /**
     * Start to build a hash configuration based on a prior configuration.
     * @param prior the prior configuration.
     */
    public Builder(final HashConfiguration prior)
    {
      this.cacheConfiguration = prior.cacheConfiguration;
      this.strength = prior.strength;
    }

    public Builder cacheConfiguration(final CacheConfiguration cacheConfiguration)
    {
      this.cacheConfiguration = cacheConfiguration;
      return this;
    }

    public Builder strength(final Integer strength)
    {
      this.strength = strength;
      return this;
    }

    public HashConfiguration build()
    {
      return new HashConfiguration(this.cacheConfiguration, this.strength);
    }
  }
}
