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

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.flow.repositories.FlowRepository;
import com.wealdtech.flow.repositories.FlowRepositoryPostgreSqlImpl;
import com.wealdtech.flow.tasks.SendMessageTask;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 *
 */
public class WorkerModule extends AbstractModule
{
  private static final Logger LOG = LoggerFactory.getLogger(WorkerModule.class);

  @Override
  protected void configure()
  {
    // Bind configuration information
    final WorkerConfiguration configuration =
        new ConfigurationSource<WorkerConfiguration>().getConfiguration("worker-config.json", WorkerConfiguration.class);
    bind(WorkerConfiguration.class).toInstance(configuration);


    bind(PostgreSqlConfiguration.class).annotatedWith(Names.named("flowrepositoryconfiguration"))
                                       .toInstance(configuration.getFlowRepositoryConfiguration());
    bind(FlowRepository.class).to(FlowRepositoryPostgreSqlImpl.class).in(Singleton.class);

    bind(JavaDelegate.class).annotatedWith(Names.named("SendMessageTask")).to(SendMessageTask.class).in(Singleton.class);
  }
}
