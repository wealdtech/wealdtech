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

import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.wealdtech.DataError;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 */
public class ApplicationModule extends AbstractModule
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationModule.class);

  private final String configFile;

  public ApplicationModule(@Nullable final String configFile)
  {
    this.configFile = MoreObjects.firstNonNull(configFile, "chatd-config.json");
  }

  @Override
  protected void configure()
  {
    try
    {
      // Bind configuration information
      final ChatDConfiguration configuration = new ConfigurationSource<ChatDConfiguration>().getConfiguration(this.configFile, ChatDConfiguration.class);

      bind(JettyServerConfiguration.class).toInstance(configuration.getJettyServerConfiguration());
      bind(JerseyServerConfiguration.class).toInstance(configuration.getJerseyServerConfiguration());
      bind(PostgreSqlConfiguration.class).toInstance(configuration.getPostgreSqlConfiguration());
    }
    catch (final DataError de)
    {
      LOGGER.error("Failed to initialize properties: {}", de.getLocalizedMessage(), de);
      System.exit(-1);
    }
  }
}
