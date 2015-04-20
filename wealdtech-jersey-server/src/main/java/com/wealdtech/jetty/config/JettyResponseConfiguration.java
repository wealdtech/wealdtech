/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jetty.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a Jetty response.
 * <p>
 * A Jetty response contains a server header as well as the retry period in case of failures.
 */
public final class JettyResponseConfiguration implements Configuration
{
  private String serverName = "Weald Technology server";

  private int retryPeriod = 60;

  public JettyResponseConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettyResponseConfiguration(@JsonProperty("servername") final String serverName,
                                     @JsonProperty("retryperiod") final Integer retryPeriod)
  {
    this.serverName = MoreObjects.firstNonNull(serverName, this.serverName);
    this.retryPeriod = MoreObjects.firstNonNull(retryPeriod, this.retryPeriod);
  }

  public String getServerName()
  {
    return this.serverName;
  }

  public int getRetryPeriod()
  {
    return this.retryPeriod;
  }
}
