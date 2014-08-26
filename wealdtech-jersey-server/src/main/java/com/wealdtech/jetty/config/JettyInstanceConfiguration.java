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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a Jetty instance.
 * <p>
 * A Jetty instance consists of a number of connectors which share a common threadpool
 * and serve a common set of resources.
 */
public final class JettyInstanceConfiguration implements Configuration
{
  private String name = "server";
  private JettyThreadPoolConfiguration threadPoolConfiguration = new JettyThreadPoolConfiguration();
  private JettySslConfiguration sslConfiguration = new JettySslConfiguration();
  private ImmutableList<JettyConnectorConfiguration> connectorConfigurations = ImmutableList.of(new JettyConnectorConfiguration());

  @Inject
  public JettyInstanceConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private JettyInstanceConfiguration(@JsonProperty("name") final String name,
                                     @JsonProperty("threadpool") final JettyThreadPoolConfiguration threadPoolConfiguration,
                                     @JsonProperty("connectors") final List<JettyConnectorConfiguration> connectorConfigurations,
                                     @JsonProperty("ssl") final JettySslConfiguration sslConfiguration)
  {
    this.name = Objects.firstNonNull(name, this.name);
    this.threadPoolConfiguration = Objects.firstNonNull(threadPoolConfiguration, this.threadPoolConfiguration);
    this.connectorConfigurations = ImmutableList.copyOf(Objects.firstNonNull(connectorConfigurations, this.connectorConfigurations));
    this.sslConfiguration = Objects.firstNonNull(sslConfiguration, this.sslConfiguration);
  }

  public String getName()
  {
    return this.name;
  }

  public JettyThreadPoolConfiguration getThreadPoolConfiguration()
  {
    return this.threadPoolConfiguration;
  }

  public ImmutableList<JettyConnectorConfiguration> getConnectorConfigurations()
  {
    return this.connectorConfigurations;
  }

  public JettySslConfiguration getSslConfiguration()
  {
    return this.sslConfiguration;
  }
}
