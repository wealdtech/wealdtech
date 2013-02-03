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

package com.wealdtech.configuration;

import ch.qos.logback.classic.Level;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

/**
 * Configuration for a logging system.
 */
public class LoggingConfiguration
{
  private Level level = Level.INFO;

  @Inject
  public LoggingConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private LoggingConfiguration(@JsonProperty("level") final Level level)
  {
    if (level != null)
    {
      this.level = level;
    }
  }

  public Level getLevel()
  {
    return this.level;
  }

  public static class Builder
  {
    private Level level;

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
    }

    public Builder level(final Level level)
    {
      this.level = level;
      return this;
    }

    public LoggingConfiguration build()
    {
      return new LoggingConfiguration(this.level);
    }
  }
}
