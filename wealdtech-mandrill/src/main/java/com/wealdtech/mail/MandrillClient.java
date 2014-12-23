/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wealdtech.mail.config.MandrillConfiguration;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import java.util.List;
import java.util.Map;

/**
 * Retrofit client for accessing the Mandrill API
 */
public class MandrillClient
{
  private static final Logger LOG = LoggerFactory.getLogger(MandrillClient.class);

  private static final String ENDPOINT = "https://mandrillapp.com/api/1.0";

  private final MandrillConfiguration configuration;

  public final MandrillService service;

  @Inject
  private MandrillClient(final MandrillConfiguration configuration)
  {
    this.configuration = configuration;

    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT)
                                                         .setConverter(converter)
                                                         .setLogLevel(RestAdapter.LogLevel.FULL)
                                                         .build();
    this.service = adapter.create(MandrillService.class);
  }

  public List<MandrillSendResponse> sendTemplate(final String template, final Map<String, String> data, final ImmutableList<MailActor> recipients)
  {
    final MandrillSendRequest request = new MandrillSendRequest(configuration.getKey(), template, data, new MandrillMessage.Builder().recipients(recipients).sender(configuration.getSender()).build());
    return service.sendTemplate(request);
  }

  public boolean ping()
  {
    return "PONG!".equals(service.ping(ImmutableMap.<String, String>of("key", configuration.getKey())));
  }
}

