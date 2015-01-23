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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.wealdtech.DataError;
import com.wealdtech.chat.repositories.MessageRepository;
import com.wealdtech.chat.repositories.MessageRepositoryPostgreSqlImpl;
import com.wealdtech.chat.repositories.SubscriptionRepository;
import com.wealdtech.chat.repositories.SubscriptionRepositoryPostgreSqlImpl;
import com.wealdtech.chat.services.MessageService;
import com.wealdtech.chat.services.MessageServicePostgreSqlImpl;
import com.wealdtech.chat.services.SubscriptionService;
import com.wealdtech.chat.services.SubscriptionServicePostgreSqlImpl;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import com.wealdtech.notifications.config.NotificationConfiguration;
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
      // Use the Weald object mapper
      bind(ObjectMapper.class).toInstance(WealdMapper.getServerMapper());

      // Bind configuration information
      final ChatDConfiguration configuration = new ConfigurationSource<ChatDConfiguration>().getConfiguration(this.configFile, ChatDConfiguration.class);
      bind(JettyServerConfiguration.class).toInstance(configuration.getJettyServerConfiguration());
      bind(JerseyServerConfiguration.class).toInstance(configuration.getJerseyServerConfiguration());
      bind(PostgreSqlConfiguration.class).toInstance(configuration.getPostgreSqlConfiguration());
      bind(NotificationConfiguration.class).toInstance(configuration.getNotificationsConfiguration());

      // Bind Chat service to use PostgreSql
      bind(PostgreSqlConfiguration.class).annotatedWith(Names.named("messagerepositoryconfiguration"))
                                         .toInstance(configuration.getPostgreSqlConfiguration());
      bind(MessageRepository.class).to(MessageRepositoryPostgreSqlImpl.class).in(Singleton.class);
      bind(MessageService.class).to(MessageServicePostgreSqlImpl.class).in(Singleton.class);

      // Bind Subscription service to use PostgreSql
      bind(PostgreSqlConfiguration.class).annotatedWith(Names.named("subscriptionrepositoryconfiguration"))
                                         .toInstance(configuration.getPostgreSqlConfiguration());
      bind(SubscriptionRepository.class).to(SubscriptionRepositoryPostgreSqlImpl.class).in(Singleton.class);
      bind(SubscriptionService.class).to(SubscriptionServicePostgreSqlImpl.class).in(Singleton.class);
    }
    catch (final DataError de)
    {
      LOGGER.error("Failed to initialize properties: {}", de.getLocalizedMessage(), de);
      System.exit(-1);
    }
  }
}
