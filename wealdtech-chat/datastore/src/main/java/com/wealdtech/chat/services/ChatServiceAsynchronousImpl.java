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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.wealdtech.Application;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.chat.events.MessageEvent;
import com.wealdtech.services.WIDService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.wealdtech.Preconditions.checkState;

/**
 * An implementation of the chat service which handles notifications asynchronously
 */
public class ChatServiceAsynchronousImpl implements ChatService
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageServicePostgreSqlImpl.class);

  private final EventBus eventBus;
  private final MessageService messageService;
  private final SubscriptionService subscriptionService;
  private final TopicService topicService;
  private final WIDService widService;

  @Inject
  public ChatServiceAsynchronousImpl(final EventBus eventBus,
                                     final MessageService messageService,
                                     final SubscriptionService subscriptionService,
                                     final TopicService topicService,
                                     final WIDService widService)
  {
    this.eventBus = eventBus;
    this.messageService = messageService;
    this.subscriptionService = subscriptionService;
    this.topicService = topicService;
    this.widService = widService;
  }

  @Override
  public void createTopic(final Application app, final User user, final Topic topic)
  {
    topicService.create(app, topic);
  }

  @Override
  public void updateTopic(final Application app, final User user, final Topic topic)
  {
    topicService.update(app, topic);
  }

  @Override
  @Nullable
  public Topic obtainTopic(final Application app, final User user, final WID<Topic> topicId)
  {
    return topicService.obtain(app, topicId);
  }

  @Override
  public void removeTopic(final Application app, final User user, final Topic topic)
  {
    topicService.remove(app, topic);
  }

  @Override
  public Message obtainMessage(final Application app, final User user, final WID<Topic> topicId, final WID<Message> messageId)
  {
    // Obtain the topic
    final Topic topic = topicService.obtain(app, topicId);
    checkState(topic != null, "Unknown topic");
    checkState(topic.getParticipantIds().contains(user.getId()), "User is not subscribed to that topic");

    return messageService.obtain(app, user, topic, messageId);
  }

  @Override
  public ImmutableList<Message> obtainMessagesSince(final Application app,
                                                    final User user,
                                                    final WID<Topic> topicId,
                                                    @Nullable final DateTime since)
  {
    // Obtain the topic
    final Topic topic = topicService.obtain(app, topicId);
    checkState(topic != null, "Unknown topic");
    checkState(topic.getParticipantIds().contains(user.getId()), "User is not subscribed to that topic");

    return messageService.obtainTo(app, user, topic, since);
  }

  @Override
  public void createMessage(final Application app, final User user, final WID<Topic> topicId, final Message message)
  {
    // Obtain the topic
    Topic topic = topicService.obtain(app, topicId);
    if (topic == null)
    {
      // This means that this is the first message in the topic: auto-create
      topic = Topic.builder()
                   .id(topicId)
                   .name("Unnamed conversation")
                   .ownerIds(ImmutableSet.of(message.getFrom()))
                   .participantIds(ImmutableSet.of(message.getFrom()))
                   .build();
      topicService.create(app, topic);
    }
    else
    {
      checkState(topic.getParticipantIds().contains(user.getId()), "User is not subscribed to that topic");
    }
    messageService.create(app, user, topic, message);
    eventBus.post(new MessageEvent(MessageEvent.Type.CREATED, app.getId(), topicId, message));
  }
}
