/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.weather.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.ServerError;
import com.wealdtech.configuration.Configuration;

/**
 */
public class WeatherConfiguration implements Configuration
{
  private final String apiKey;

  public WeatherConfiguration()
  {
    throw new ServerError("Not allowed to create weather services configuration without information");
  }

  @JsonCreator
  public WeatherConfiguration(@JsonProperty("apikey") final String apiKey)
  {
    this.apiKey = apiKey;
  }

  public String getApiKey()
  {
    return this.apiKey;
  }

  /**
   * Obtain a weather configuration from the environment
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static WeatherConfiguration fromEnv(final String base)
  {
    return new WeatherConfiguration(System.getenv(base + "_apikey"));
  }
}
