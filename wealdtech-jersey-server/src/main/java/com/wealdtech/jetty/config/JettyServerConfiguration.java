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

package com.wealdtech.jetty.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a Jetty server.
 * <p>
 * A Jetty server consists of a number of instances of {@link JettyInstanceConfiguration}.
 *
 * @see JettyInstanceConfiguration
 */
public final class JettyServerConfiguration implements Configuration
{
  private ImmutableList<JettyInstanceConfiguration> instanceConfigurations = ImmutableList.of(new JettyInstanceConfiguration());

  @Inject
  public JettyServerConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private JettyServerConfiguration(@JsonProperty("instances") final List<JettyInstanceConfiguration> instanceConfigurations)
  {
    this.instanceConfigurations = ImmutableList.copyOf(Objects.firstNonNull(instanceConfigurations, this.instanceConfigurations));
  }

  public ImmutableList<JettyInstanceConfiguration> getInstanceConfigurations()
  {
    return this.instanceConfigurations;
  }
}
