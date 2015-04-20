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
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for Jersey server
 */
public class JerseyServerConfiguration implements Configuration
{
  // CORS configuration
  private CORSConfiguration corsConfiguration = new CORSConfiguration();

  @Inject
  public JerseyServerConfiguration()
  {

  }

  @JsonCreator
  private JerseyServerConfiguration(@JsonProperty("cors") final CORSConfiguration corsConfiguration)
  {
    this.corsConfiguration = MoreObjects.firstNonNull(corsConfiguration, this.corsConfiguration);
  }

  public CORSConfiguration getCORSConfiguration()
  {
    return this.corsConfiguration;
  }
}
