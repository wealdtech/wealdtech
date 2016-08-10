/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
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
import com.wealdtech.configuration.Configuration;

import java.net.URI;

/**
 * Configuration information for MangoPay
 */
public class MangoPayConfiguration implements Configuration
{
  private final String clientId;
  private final String secret;
  private final URI endpoint;

  @JsonCreator
  public MangoPayConfiguration(@JsonProperty("clientid") final String clientId,
                               @JsonProperty("secret") final String secret,
                               @JsonProperty("endpoint") final URI endpoint)
  {
    this.clientId = clientId;
    this.secret = secret;
    this.endpoint = endpoint;
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static MangoPayConfiguration fromEnv(final String base)
  {
    final URI endpoint = System.getenv(base + "_endpoint") == null ? null : URI.create(System.getenv(base + "_endpoint"));
    return new MangoPayConfiguration(System.getenv(base + "_clientid"), System.getenv(base + "_secret"), endpoint);
  }

  public String getClientId(){ return clientId; }

  public String getSecret(){ return secret; }

  public URI getEndpoint(){ return endpoint; }
}
