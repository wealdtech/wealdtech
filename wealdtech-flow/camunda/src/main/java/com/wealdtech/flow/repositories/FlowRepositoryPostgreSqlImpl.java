/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.flow.repositories;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;

/**
 *
 */
public class FlowRepositoryPostgreSqlImpl implements FlowRepository
{
  private final PostgreSqlConfiguration configuration;

  @Inject
  public FlowRepositoryPostgreSqlImpl(@Named("flowrepositoryconfiguration") final PostgreSqlConfiguration configuration)
  {
    this.configuration = configuration;
  }

  @Override
  public ProcessEngine getConnection()
  {
    final ProcessEngineConfigurationImpl processEngineConfiguration;
    processEngineConfiguration =
        (ProcessEngineConfigurationImpl)ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                                                                  .setJdbcDriver("org.postgresql.Driver")
                                                                  .setJdbcUrl("jdbc:postgresql://" + configuration.getHost() + "/" + configuration.getName())
                                                                  .setJdbcUsername(configuration.getUsername())
                                                                  .setJdbcPassword(configuration.getPassword())
                                                                  .setDatabaseSchemaUpdate(
                                                                      ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                                                                  .setHistory(ProcessEngineConfiguration.HISTORY_FULL)
                                                                  .setJobExecutorActivate(true);
    processEngineConfiguration.setDefaultSerializationFormat("application/json");
    processEngineConfiguration.setProcessEnginePlugins(ImmutableList.<ProcessEnginePlugin>of(new SpinProcessEnginePlugin()));
    return processEngineConfiguration.buildProcessEngine();
  }
}
