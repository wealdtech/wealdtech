/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.GenericWObject;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.RetrofitHelper;
import com.wealdtech.services.config.FcmConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Callback;

/**
 *
 */
public class FcmClient
{
  private static final Logger LOG = LoggerFactory.getLogger(FcmClient.class);

  private static final String ENDPOINT = "https://fcm.googleapis.com/fcm/";

  private final FcmConfiguration configuration;

  public final FcmService service;

  @Inject
  public FcmClient(final FcmConfiguration configuration)
  {
    this.configuration = configuration;
    this.service = RetrofitHelper.createRetrofit(ENDPOINT, FcmService.class);
  }

  /**
   * Send a message.
   * This operates asynchronously
   * @param recipients the recipients of the message
   * @param message the message
   */
  public void sendMessage(final ImmutableList<String> recipients, final WObject<?> message, final Callback<GenericWObject> cb)
  {
    service.sendMessage(auth(configuration.getApiKey()),
                        GenericWObject.builder().data("registration_ids", recipients).data("data", message).build()).enqueue(cb);
  }

  private static String auth(final String key) { return "key=" + key; }
}
