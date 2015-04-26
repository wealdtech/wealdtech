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

import com.wealdtech.WID;
import com.wealdtech.chat.Application;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

/**
 * Client forWealdtech Chat
 */
public class ChatClient
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatClient.class);

  private static final String ENDPOINT = "http://localhost:8080";

  private static volatile ChatClient instance = null;

  public final ChatService service;

  public static ChatClient getInstance()
  {
    if (instance == null)
    {
      synchronized (ChatClient.class)
      {
        if (instance == null)
        {
          instance = new ChatClient();
        }
      }
    }
    return instance;
  }

  private ChatClient()
  {
    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).setLogLevel(RestAdapter.LogLevel.FULL).build();
    this.service = adapter.create(ChatService.class);
  }

  /**
   * Create a message
   */
  public void createMessage(final WID<Application> appId, final WID<Topic> topicId, final Message message)
  {
    service.createMessage(appId.toString(), topicId.toString(), message);
  }

  /**
   * Obtain a message
   */
  public Message obtainMessage(final WID<Application> appId, final WID<Topic> topicId, final WID<Message> messageId)
  {
    return service.obtainMessage(appId.toString(), topicId.toString(), messageId.toString());
  }
}
