/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.chat;

import com.wealdtech.chat.Message;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Interact with the Wealdtech Chat API
 */
public interface ChatService
{
  /**
   * Send a message to a particular topic
   */
  @POST("/{appid}/topics/{topicid}/messages")
  Response createMessage(@Path("appid") final String appId, @Path("topicid") final String topicId, @Body final Message message);

  /**
   * Obtain a specific message
   */
  @GET("/{appid}/topics/{topicid}/messages/{messageid}")
  Message obtainMessage(@Path("appid") final String appId,
                        @Path("topicid") final String topicId,
                        @Path("messageid") final String messageId);
}
