/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.gcm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.GenericWObject;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import com.wealdtech.services.config.GcmConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

/**
 * Client for Google Cloud Messaging
 */
public class GcmClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GcmClient.class);

  private static final String ENDPOINT = "https://gcm-http.googleapis.com/gcm/";

  private final GcmConfiguration configuration;

  public final GcmService service;

  @Inject
  public GcmClient(final GcmConfiguration configuration)
  {
    this.configuration = configuration;
    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).setLogLevel(RestAdapter.LogLevel.FULL).build();
    this.service = adapter.create(GcmService.class);
  }

  /**
   * Send a message
   * @param recipients the recipients of the message
   * @param message the message
   */
  public void sendMessage(final ImmutableSet<String>recipients, final WObject<?> message)
  {
    service.sendMessage(auth(configuration.getApiKey()),
                        GenericWObject.builder().data("registration_ids", recipients).data("data", message).build());
  }

  private static String auth(final String key)
  {
    return "key=" + key;
  }
}
