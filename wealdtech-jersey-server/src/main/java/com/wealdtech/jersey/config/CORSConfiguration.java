/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for cross-origin resource sharing in Jersey server
 */
public class CORSConfiguration implements Configuration
{
  // CORS configuration
  private String origin = "*";

  private boolean allowCredentials = true;

  private String allowedMethods = "GET, POST, PUT, DELETE, OPTIONS";

  private Boolean reflectRequest = false;

  @Inject
  public CORSConfiguration()
  {
  }

  @JsonCreator
  private CORSConfiguration(@JsonProperty("origin") final String origin,
                            @JsonProperty("allowcredentials") final Boolean allowCredentials,
                            @JsonProperty("allowedmethods") final String allowedMethods,
                            @JsonProperty("reflectrequest") final Boolean reflectRequest)
  {
    this.origin = Objects.firstNonNull(origin, this.origin);
    this.allowCredentials = Objects.firstNonNull(allowCredentials, this.allowCredentials);
    this.allowedMethods = Objects.firstNonNull(allowedMethods, this.allowedMethods);
    this.reflectRequest = Objects.firstNonNull(reflectRequest, this.reflectRequest);
  }

  public String getOrigin()
  {
    return this.origin;
  }

  public boolean allowCredentials()
  {
    return this.allowCredentials;
  }

  public String getAllowedMethods()
  {
    return this.allowedMethods;
  }

  public boolean reflectRequest()
  {
    return this.reflectRequest;
  }
}
