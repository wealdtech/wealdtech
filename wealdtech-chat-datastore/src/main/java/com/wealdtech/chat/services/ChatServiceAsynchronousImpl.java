/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.DataError;
import com.wealdtech.GenericWObject;
import com.wealdtech.ServerError;
import com.wealdtech.WID;
import com.wealdtech.chat.Application;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * An implementation of the chat service which handles notifications asynchronously
 */
public class ChatServiceAsynchronousImpl implements ChatService
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageServicePostgreSqlImpl.class);

  private final MessageService messageService;
  private final NotificationService notificationService;
  private final SubscriptionService subscriptionService;
  private final TopicService topicService;

  @Inject
  public ChatServiceAsynchronousImpl(final MessageService messageService,
                                     final NotificationService notificationService,
                                     final SubscriptionService subscriptionService,
                                     final TopicService topicService)
  {
    this.notificationService = notificationService;
    this.messageService = messageService;
    this.subscriptionService = subscriptionService;
    this.topicService = topicService;
  }

  @Override
  public void createTopic(final WID<Application> appId,
                          final Topic topic)
  {
    topicService.create(appId, topic);
  }

  @Override
  public void updateTopic(final WID<Application> appId,
                          final Topic topic)
  {
    topicService.update(appId, topic);
  }

  @Override
  @Nullable
  public Topic obtainTopic(final WID<Application> appId,
                          final WID<Topic> topicId)
  {
    return topicService.obtain(appId, topicId);
  }

  @Override
  public Message obtainMessage(final WID<Application> appId,
                               final WID<Topic> topicId,
                               final WID<Message> messageId)
  {
    return messageService.obtain(appId, topicId, messageId);
  }

  @Override
  public void createMessage(final WID<Application> appId,
                            final WID<Topic> topicId,
                            final Message message)
  {
    messageService.create(appId, topicId, message);
    // Obtain recipients given the message
    ImmutableSet<String> recipients;
    switch(message.getScope())
    {
      case INDIVIDUAL:
        recipients = ImmutableSet.of(message.getTo().iterator().next().toString());
        break;
      case FRIENDS:
        throw new ServerError("Not supported");
//        break;
      case EVERYONE:
        throw new ServerError("Not supported");
//        break;
      default:
        throw new DataError.Bad("Unhandled message scope \"" + message.getScope().toString() + "\"");
    }

    // Create the message
    final GenericWObject msg = GenericWObject.builder()
                                             .data("timestamp", message.getTimestamp().getMillis())
                                             .data("topic", topicId.toString())
                                             .data("msg", message.getId())
                                             .build();
    notificationService.notify(recipients, msg);
  }
}
