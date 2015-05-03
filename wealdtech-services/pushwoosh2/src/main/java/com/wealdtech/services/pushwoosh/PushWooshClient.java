/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.pushwoosh;

import com.google.common.collect.ImmutableSet;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

/**
 * Client for Pushwoosh
 */
public class PushWooshClient
{
  private static final Logger LOG = LoggerFactory.getLogger(PushWooshClient.class);

  private static final String ENDPOINT = "https://cp.pushwoosh.com/json/1.3/";

  private static volatile PushWooshClient instance = null;

  public final PushWooshService service;

  public static PushWooshClient getInstance()
  {
    if (instance == null)
    {
      synchronized (PushWooshClient.class)
      {
        if (instance == null)
        {
          instance = new PushWooshClient();
        }
      }
    }
    return instance;
  }

  private PushWooshClient()
  {
    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).setLogLevel(RestAdapter.LogLevel.FULL).build();
    this.service = adapter.create(PushWooshService.class);
  }

  /**
   * Send a message
   */
  public void sendMessage(final ImmutableSet<String>recipients, final WObject<?> message)
  {
    final PushWooshBody body;
    body = PushWooshBody.builder()
                        .request(PushWooshRequest.builder()
                                                 .application("096F1-E38E8")
                                                 .auth("8q8iYJNp9p6ejUCOuLu8S2LXBSQ5JZm2TZlAiyaXPdIjqShZWUFcICOFgoYeqrcKKHlueR7e13RaC9Gv2lWO")
                                                 .notifications(ImmutableSet.of(PushWooshNotification.builder()
                                                                                                     .sendDate("now")
                                                                                                     .ignoreUserTimezone(true)
                                                                                                     .data(message)
                                                                                                     .devices(recipients)
                                                                                                     .build()))
                                                 .build())
                        .build();
    service.sendMessage(body);
  }
}
