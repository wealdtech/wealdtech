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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wealdtech.DataError;
import com.wealdtech.config.WIDConfiguration;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.jersey.config.JerseyServerConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import com.wealdtech.services.NlpService;
import com.wealdtech.services.NlpServiceNlp4JImpl;
import com.wealdtech.services.WIDServiceLocalModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;

/**
 *
 */
public class NlpDModule extends AbstractModule
{
  private static final Logger LOG = LoggerFactory.getLogger(NlpDModule.class);

  private final String configFile;

  public NlpDModule(@Nullable final String configFile)
  {
    this.configFile = MoreObjects.firstNonNull(configFile, "nlpd-config.json");
  }

  @Override
  protected void configure()
  {
    try
    {
      // Use the Weald object mapper
      bind(ObjectMapper.class).toInstance(WealdMapper.getServerMapper());

      // Bind configuration information
      final NlpDConfiguration configuration =
          new ConfigurationSource<NlpDConfiguration>().getConfiguration(this.configFile, NlpDConfiguration.class);
      bind(NlpDConfiguration.class).toInstance(configuration);
      bind(JettyServerConfiguration.class).toInstance(configuration.getJettyServerConfiguration());
      bind(JerseyServerConfiguration.class).toInstance(configuration.getJerseyServerConfiguration());

      // We have multiple different object mappers.  The database-facing mapper users longs for timestamps for efficiency when
      // searching for values
      bind(ObjectMapper.class).annotatedWith(Names.named("dbmapper"))
                              .toInstance(WealdMapper.getServerMapper()
                                                     .copy()
                                                     .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

      // Use a local WID module
      bind(WIDConfiguration.class).toInstance(configuration.getWIDConfiguration());
      install(new WIDServiceLocalModule());

      // Use the NLP4J service for processing
      bind(NlpService.class).to(NlpServiceNlp4JImpl.class).in(Singleton.class);
    }
    catch (final DataError de)
    {
      LOG.error("Failed to initialize properties: {}", de.getLocalizedMessage(), de);
      System.exit(-1);
    }
  }

}
