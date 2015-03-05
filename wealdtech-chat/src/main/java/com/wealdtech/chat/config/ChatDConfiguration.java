/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.config.LoggingConfiguration;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import com.wealdtech.notifications.config.NotificationConfiguration;

/**
 * Configuration for the various aspects of the Chat daemon
 */
public class ChatDConfiguration implements Configuration
{
  private final JettyServerConfiguration jettyServerConfiguration;
  private final JerseyServerConfiguration jerseyServerConfiguration;
  private final LoggingConfiguration loggingConfiguration;
  private final PostgreSqlConfiguration postgreSqlConfiguration;
  private final NotificationConfiguration notificationsConfiguration;

  public ChatDConfiguration(@JsonProperty("jetty") final JettyServerConfiguration jettyServerConfiguration,
                            @JsonProperty("jersey") final JerseyServerConfiguration jerseyServerConfiguration,
                            @JsonProperty("logging") final LoggingConfiguration loggingConfiguration,
                            @JsonProperty("datastore") final PostgreSqlConfiguration postgreSqlConfiguration,
                            @JsonProperty("notifications") final NotificationConfiguration notificationsConfiguration)
  {
    this.jettyServerConfiguration = jettyServerConfiguration;
    this.jerseyServerConfiguration = jerseyServerConfiguration;
    this.loggingConfiguration = loggingConfiguration;
    this.postgreSqlConfiguration = postgreSqlConfiguration;
    this.notificationsConfiguration = notificationsConfiguration;
  }

  public JettyServerConfiguration getJettyServerConfiguration() { return jettyServerConfiguration; }

  public JerseyServerConfiguration getJerseyServerConfiguration() { return jerseyServerConfiguration; }

  public LoggingConfiguration getLoggingConfiguration() { return loggingConfiguration; }

  public PostgreSqlConfiguration getPostgreSqlConfiguration() { return postgreSqlConfiguration; }

  public NotificationConfiguration getNotificationsConfiguration() { return notificationsConfiguration; }
}
