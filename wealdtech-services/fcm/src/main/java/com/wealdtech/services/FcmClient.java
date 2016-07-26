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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.GenericWObject;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.RetrofitHelper;
import com.wealdtech.services.config.FcmConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

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
  public void sendMessage(final ImmutableSet<String> recipients, final WObject<?> message)
  {
    final Call<GenericWObject> call = service.sendMessage(auth(configuration.getApiKey()), GenericWObject.builder()
                                                                                                   .data("registration_ids",
                                                                                                         recipients)
                                                                                                   .data("data", message)
                                                                                                   .build());
    call.enqueue(new Callback<GenericWObject>(){
      @Override
      public void onResponse(final Call<GenericWObject> call, final Response<GenericWObject> response)
      {
        if (!response.isSuccessful())
        {
          LOG.error("Response status is {}", response.code());
          try
          {
            LOG.error("Error body is {}", response.errorBody().string());
          }
          catch (final IOException ignored) {}
        }
        else
        {
          LOG.debug("Response is {}", response.body());
        }
      }

      @Override
      public void onFailure(final Call<GenericWObject> call, final Throwable t)
      {
        LOG.error("Attempt to send message failed: ", t);
      }
    });
  }

  private static String auth(final String key) { return "key=" + key; }
}
