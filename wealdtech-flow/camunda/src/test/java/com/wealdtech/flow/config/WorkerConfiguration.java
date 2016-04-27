/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.flow.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;

/**
 *
 */
public class WorkerConfiguration implements Configuration
{
  private final String name;
  private final PostgreSqlConfiguration flowRepositoryConfiguration;

  @JsonCreator
  private WorkerConfiguration(@JsonProperty("name") final String name,
                              @JsonProperty("flowrepository") final PostgreSqlConfiguration flowRepositoryConfiguration)
  {
    this.name = name;
    this.flowRepositoryConfiguration = flowRepositoryConfiguration;
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static WorkerConfiguration fromEnv(final String base)
  {
    return new WorkerConfiguration(System.getenv(base + "_productid"), PostgreSqlConfiguration.fromEnv(base + "_flowrepository"));
  }

  @JsonProperty("name")
  public String getName(){ return name; }

  @JsonProperty("flowrepository")
  public PostgreSqlConfiguration getFlowRepositoryConfiguration(){ return flowRepositoryConfiguration; }
}
