/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.configuration;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Configuration for a logging system.
 */
public class LoggingConfiguration implements Configuration
{
  private Level level = Level.INFO;
  private ImmutableMap<String, Level> overrides = ImmutableMap.of();
  private String pattern = "%d{yyyyMMdd'T'HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

  @Inject
  public LoggingConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private LoggingConfiguration(@JsonProperty("level") final Level level,
                               @JsonProperty("overrides") final ImmutableMap<String, Level> overrides,
                               @JsonProperty("pattern") final String pattern)
  {
    this.level = Objects.firstNonNull(level, this.level);
    this.overrides = Objects.firstNonNull(overrides, this.overrides);
    this.pattern = Objects.firstNonNull(pattern, this.pattern);
  }

  public Level getLevel()
  {
    return this.level;
  }

  public ImmutableMap<String, Level> getOverrides()
  {
    return this.overrides;
  }

  public String getPattern()
  {
    return this.pattern;
  }

  public static class Builder
  {
    private Level level;
    private ImmutableMap<String, Level> overrides;
    private String pattern;

    /**
     * Start to build a logging configuration.
     */
    public Builder()
    {
      // Nothing to do
    }

    /**
     * Start to build a logging configuration based on a prior configuration.
     * @param prior the prior configuration.
     */
    public Builder(final LoggingConfiguration prior)
    {
      this.level = prior.level;
      this.overrides = prior.overrides;
      this.pattern = prior.pattern;
    }

    public Builder level(final Level level)
    {
      this.level = level;
      return this;
    }

    public Builder overrides(final ImmutableMap<String, Level> overrides)
    {
      this.overrides = overrides;
      return this;
    }

    public Builder pattern(final String pattern)
    {
      this.pattern = pattern;
      return this;
    }

    public LoggingConfiguration build()
    {
      return new LoggingConfiguration(this.level, this.overrides, this.pattern);
    }
  }
}
