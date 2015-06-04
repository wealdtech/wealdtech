/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.wealdtech.DataError;
import com.wealdtech.TwoTuple;
import com.wealdtech.User;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.config.WIDConfiguration;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repositories.ApplicationRepository;
import com.wealdtech.datastore.repositories.ApplicationRepositoryPostgreSqlImpl;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.jersey.auth.Authenticator;
import com.wealdtech.jersey.auth.WealdBasicAuthenticator;
import com.wealdtech.jersey.auth.WealdTokenAuthenticator;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import com.wealdtech.repositories.UserRepository;
import com.wealdtech.repositories.UserRepositoryPostgreSqlImpl;
import com.wealdtech.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

//import com.wealdtech.notifications.providers.NotificationProvider;
//import com.wealdtech.notifications.providers.NotificationProviderPushWooshImpl;
//import com.wealdtech.services.config.PushWooshConfiguration;

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
      final UserDConfiguration configuration =
          new ConfigurationSource<UserDConfiguration>().getConfiguration(this.configFile, UserDConfiguration.class);
      bind(JettyServerConfiguration.class).toInstance(configuration.getJettyServerConfiguration());
      bind(JerseyServerConfiguration.class).toInstance(configuration.getJerseyServerConfiguration());
      bind(PostgreSqlConfiguration.class).toInstance(configuration.getPostgreSqlConfiguration());
//      bind(PushWooshConfiguration.class).toInstance(configuration.getPushWooshConfiguration());
//      bind(NotificationProvider.class).to(NotificationProviderPushWooshImpl.class).in(Singleton.class);

      // We have multiple different object mappers.  The database-facing mapper users longs for timestamps for efficiency when
      // searching for values
      bind(ObjectMapper.class).annotatedWith(Names.named("dbmapper"))
                              .toInstance(WealdMapper.getServerMapper()
                                                     .copy()
                                                     .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

      // Bind Application service to use PostgreSql
      bind(PostgreSqlConfiguration.class).annotatedWith(Names.named("applicationrepositoryconfiguration"))
                                         .toInstance(configuration.getPostgreSqlConfiguration());
      bind(ApplicationRepository.class).to(ApplicationRepositoryPostgreSqlImpl.class).in(Singleton.class);
      bind(ApplicationService.class).to(ApplicationServicePostgreSqlImpl.class).in(Singleton.class);

      // Bind User service to use PostgreSql
      bind(PostgreSqlConfiguration.class).annotatedWith(Names.named("userrepositoryconfiguration"))
                                         .toInstance(configuration.getPostgreSqlConfiguration());
      bind(UserRepository.class).to(UserRepositoryPostgreSqlImpl.class).in(Singleton.class);
      bind(UserService.class).to(UserServicePostgreSqlImpl.class).in(Singleton.class);

      // Use a local WID module
      bind(WIDConfiguration.class).toInstance(configuration.getWIDConfiguration());
      install(new WIDServiceLocalModule());

      // Use the asynchronous chat service
//      bind(ChatService.class).to(ChatServiceAsynchronousImpl.class).in(Singleton.class);

      bind(new TypeLiteral<Authenticator<TwoTuple<User, UserAuthorisation>>>() {}).annotatedWith(Names.named("basicauth"))
                                                                                  .to(WealdBasicAuthenticator.class)
                                                                                  .in(Singleton.class);
      bind(new TypeLiteral<Authenticator<TwoTuple<User, UserAuthorisation>>>() {}).annotatedWith(Names.named("tokenauth"))
                                                                                  .to(WealdTokenAuthenticator.class)
                                                                                  .in(Singleton.class);
    }
    catch (final DataError de)
    {
      LOGGER.error("Failed to initialize properties: {}", de.getLocalizedMessage(), de);
      System.exit(-1);
    }
  }
}
