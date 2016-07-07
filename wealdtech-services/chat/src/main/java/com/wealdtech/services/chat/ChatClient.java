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
import com.wealdtech.ServerError;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

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
    final Converter converter = new JacksonRetrofitConverter();
    final RequestInterceptor authenticationInterceptor = new RequestInterceptor()
    {
      @Override
      public void intercept(RequestFacade request)
      {
        request.addHeader("Application-ID", appId);
        try
        {
          request.addHeader("Authorization",
                            "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e)
        {
          throw new ServerError("Unable to encode username and password for basic authorisation");
        }
      }
    };
    final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT)
                                                         .setConverter(converter)
                                                         .setRequestInterceptor(authenticationInterceptor)
                                                         .build();
    this.service = adapter.create(ChatService.class);
  }

  /**
   * Create a message
   * @param topicId the ID of the topic in which to create the message
   * @param message the message to create
   */
  public void createMessage(final WID<Topic> topicId, final Message message)
  {
    service.createMessage(topicId.toString(), message);
  }

  /**
   * Obtain a message
   * @param topicId the ID of the topic in which to obtian the message
   * @param messageId the ID of th emessage to obtain
   * @return the message; can be {@code null}
   */
  public Message obtainMessage(final WID<Topic> topicId, final WID<Message> messageId)
  {
    return service.obtainMessage(topicId.toString(), messageId.toString());
  }

  /**
   * Remove an entire topic
   * @param topicId the ID of the topic to remove
   */
  public void removeTopic(final WID<Topic> topicId)
  {
    service.removeTopic(topicId.toString());
  }
}
