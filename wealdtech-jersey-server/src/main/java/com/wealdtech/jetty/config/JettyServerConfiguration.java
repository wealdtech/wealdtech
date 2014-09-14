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
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

import java.util.List;

/**
 * Configuration for a Jetty server.
 * <p>
 * A Jetty server consists of a number of instances of {@link JettyInstanceConfiguration}.
 *
 * @see JettyInstanceConfiguration
 */
public final class JettyServerConfiguration implements Configuration
{
  private boolean bodyPrefetch = true;
  private boolean detailedThreadName = true;
  private JettyResponseConfiguration responseConfiguration = new JettyResponseConfiguration();
  private ImmutableList<JettyInstanceConfiguration> instanceConfigurations = ImmutableList.of(new JettyInstanceConfiguration());
  private String metricsEndpoint = "/admin";

  @Inject
  public JettyServerConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private JettyServerConfiguration(@JsonProperty("bodyprefetchenabled") final Boolean bodyPrefetch,
                                   @JsonProperty("detailedthreadnameenabled") final Boolean detailedThreadName,
                                   @JsonProperty("response") final JettyResponseConfiguration responseConfiguration,
                                   @JsonProperty("instances") final List<JettyInstanceConfiguration> instanceConfigurations,
                                   @JsonProperty("metricsendpoint") final String metricsEndpoint)
  {
    this.bodyPrefetch = MoreObjects.firstNonNull(bodyPrefetch, this.bodyPrefetch);
    this.detailedThreadName = MoreObjects.firstNonNull(detailedThreadName, this.detailedThreadName);
    this.responseConfiguration = MoreObjects.firstNonNull(responseConfiguration, this.responseConfiguration);
    this.instanceConfigurations = ImmutableList.copyOf(MoreObjects.firstNonNull(instanceConfigurations, this.instanceConfigurations));
    this.metricsEndpoint = MoreObjects.firstNonNull(metricsEndpoint, this.metricsEndpoint);
  }

  public boolean getBodyPrefetch()
  {
    return this.bodyPrefetch;
  }

  public boolean getDetailedThreadName()
  {
    return this.detailedThreadName;
  }

  public JettyResponseConfiguration getResponseConfiguration()
  {
    return this.responseConfiguration;
  }

  public ImmutableList<JettyInstanceConfiguration> getInstanceConfigurations()
  {
    return this.instanceConfigurations;
  }

  public String getMetricsEndpoint() { return this.metricsEndpoint; }
}
