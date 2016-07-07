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
import retrofit.http.*;

/**
 * Interact with the Wealdtech Chat API
 */
public interface ChatService
{
  /**
   * Create a message in a particular topic
   * @param topicId the ID of the topic in which to create the message
   * @param message the message to create
   * @return the result of the creation
   */
  @POST("/topics/{topicid}/messages")
  Response createMessage(@Path("topicid") final String topicId, @Body final Message message);

  /**
   * Obtain a specific message
   * @param topicId the ID of the topic in which to obtain the message
   * @param messageId the ID of the message to obtain
   * @return the message; can be {@code null}
   */
  @GET("/topics/{topicid}/messages/{messageid}")
  Message obtainMessage(@Path("topicid") final String topicId, @Path("messageid") final String messageId);

  /**
   * Remove a specific topic
   * @param topicId the Id of the topic to remove
   * @return the result of the removal
   */
  @DELETE("/topics/{topicid}")
  Response removeTopic(@Path("topicid") final String topicId);
}
