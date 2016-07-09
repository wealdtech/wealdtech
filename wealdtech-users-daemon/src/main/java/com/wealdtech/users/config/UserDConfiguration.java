/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.users.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.config.LoggingConfiguration;
import com.wealdtech.config.WIDConfiguration;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;

import javax.inject.Inject;

/**
 * Configuration for the various aspects of the User daemon
 */
public class UserDConfiguration implements Configuration
{
  private final JettyServerConfiguration jettyServerConfiguration;
  private final JerseyServerConfiguration jerseyServerConfiguration;
  private final LoggingConfiguration loggingConfiguration;
  private final PostgreSqlConfiguration postgreSqlConfiguration;
  private final WIDConfiguration widConfiguration;

  @Inject
  public UserDConfiguration(@JsonProperty("server") final JettyServerConfiguration jettyServerConfiguration,
                            @JsonProperty("jersey") final JerseyServerConfiguration jerseyServerConfiguration,
                            @JsonProperty("logging") final LoggingConfiguration loggingConfiguration,
                            @JsonProperty("repository") final PostgreSqlConfiguration postgreSqlConfiguration,
                            @JsonProperty("wid") final WIDConfiguration widConfiguration)
  {
    this.jettyServerConfiguration = jettyServerConfiguration;
    this.jerseyServerConfiguration = jerseyServerConfiguration;
    this.loggingConfiguration = loggingConfiguration;
    this.postgreSqlConfiguration = postgreSqlConfiguration;
    this.widConfiguration = widConfiguration;
  }

  public JettyServerConfiguration getJettyServerConfiguration() { return jettyServerConfiguration; }

  public JerseyServerConfiguration getJerseyServerConfiguration() { return jerseyServerConfiguration; }

  public LoggingConfiguration getLoggingConfiguration() { return loggingConfiguration; }

  public PostgreSqlConfiguration getPostgreSqlConfiguration() { return postgreSqlConfiguration; }

  public WIDConfiguration getWIDConfiguration() { return widConfiguration; }
}
