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

import com.google.inject.Inject;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.retrofit.RetrofitHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Client forWealdtech Chat
 */
public class ChatClient
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatClient.class);

  private static final String ENDPOINT = "http://localhost:8080";

  public final ChatService service;

  @Inject
  public ChatClient(final String appId, final String username, final String password)
  {
    final OkHttpClient.Builder httpClientB = new OkHttpClient.Builder();
    final Interceptor authenticationInterceptor = new Interceptor()
    {
      @Override
      public Response intercept(final Chain chain) throws IOException
      {
        final Request original = chain.request();

        // Customize the request
        final Request request = original.newBuilder()
                                  .header("Application-ID", appId)
                                  .header("Authorization", "Basic " + DatatypeConverter.printBase64Binary(
                                      (username + ":" + password).getBytes("UTF-8")))
                                  .method(original.method(), original.body())
                                  .build();

        final Response response = chain.proceed(request);

        // Customize or return the response
        return response;
      }
    };

    this.service = RetrofitHelper.createRetrofit(ENDPOINT, ChatService.class, httpClientB.build());
  }

  /**
   * Create a message
   * @param topicId the ID of the topic in which to create the message
   * @param message the message to create
   */
  public void createMessage(final WID<Topic> topicId, final Message message)
  {
    RetrofitHelper.call(service.createMessage(topicId.toString(), message));
  }

  /**
   * Obtain a message
   * @param topicId the ID of the topic in which to obtian the message
   * @param messageId the ID of th emessage to obtain
   * @return the message; can be {@code null}
   */
  public Message obtainMessage(final WID<Topic> topicId, final WID<Message> messageId)
  {
    return RetrofitHelper.call(service.obtainMessage(topicId.toString(), messageId.toString()));
  }

  /**
   * Remove an entire topic
   * @param topicId the ID of the topic to remove
   */
  public void removeTopic(final WID<Topic> topicId)
  {
    RetrofitHelper.call(service.removeTopic(topicId.toString()));
  }
}
