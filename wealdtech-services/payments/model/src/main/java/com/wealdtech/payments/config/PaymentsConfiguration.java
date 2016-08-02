/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.payments.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.ServerError;
import com.wealdtech.configuration.Configuration;

/**
 *
 */
public class PaymentsConfiguration implements Configuration
{
  private final String apiToken;
  private final String apiKey;

  public PaymentsConfiguration()
  {
    throw new ServerError("Not allowed to create payments services configuration without information");
  }

  @JsonCreator
  public PaymentsConfiguration(@JsonProperty("apitoken") final String apiToken,
                               @JsonProperty("apikey") final String apiKey)
  {
    this.apiToken = apiToken;
    this.apiKey = apiKey;
  }

  public String getApiToken()
  {
    return this.apiToken;
  }

  public String getApiKey()
  {
    return this.apiKey;
  }

  /**
   * @param base the base string to use as the prefix for obtaining environmental variables
   * @return a payments configuration from the environment
   */
  public static PaymentsConfiguration fromEnv(final String base)
  {
    return new PaymentsConfiguration(System.getenv(base + "apitoken"), System.getenv(base + "_apikey"));
  }
}
