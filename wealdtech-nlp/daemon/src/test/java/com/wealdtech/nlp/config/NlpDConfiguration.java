/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlp.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.config.LoggingConfiguration;
import com.wealdtech.config.WIDConfiguration;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;

/**
 *
 */
public class NlpDConfiguration implements Configuration
{
  private final JettyServerConfiguration jettyServerConfiguration;
  private final JerseyServerConfiguration jerseyServerConfiguration;
  private final LoggingConfiguration loggingConfiguration;
  private final WIDConfiguration widConfiguration;

  @JsonCreator
  public NlpDConfiguration(@JsonProperty("server") final JettyServerConfiguration jettyServerConfiguration,
                             @JsonProperty("jersey") final JerseyServerConfiguration jerseyServerConfiguration,
                             @JsonProperty("logging") final LoggingConfiguration loggingConfiguration,
                             @JsonProperty("wid") final WIDConfiguration widConfiguration)
  {
    this.jettyServerConfiguration = jettyServerConfiguration;
    this.jerseyServerConfiguration = jerseyServerConfiguration;
    this.loggingConfiguration = loggingConfiguration;
    this.widConfiguration = widConfiguration;
  }

  public JettyServerConfiguration getJettyServerConfiguration(){ return jettyServerConfiguration; }

  public JerseyServerConfiguration getJerseyServerConfiguration(){ return jerseyServerConfiguration; }

  public LoggingConfiguration getLoggingConfiguration(){ return loggingConfiguration; }

  public WIDConfiguration getWIDConfiguration(){ return widConfiguration; }
}