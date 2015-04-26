/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import com.wealdtech.chat.services.MessageServicePostgreSqlImpl;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repositories.PostgreSqlRepository;
import com.wealdtech.jackson.WealdMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nullable;

import static org.testng.Assert.*;

/**
 * Test for the PostgreSQL implementation of the message service
 */
public class MessageServicePostgreSqlImplTest
{
  MessageServicePostgreSqlImpl messageService;
  PostgreSqlRepository repo;

  private static final WID<Application> appId = WID.<Application>generate();

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "chat", "chat", "chat", null, null, null));

    messageService = new MessageServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                             .copy()
                                                                             .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    messageService.createDatastore();
  }

  @AfterClass
  public void tearDown()
  {
    if (messageService != null)
    {
      messageService.destroyDatastore();
    }
  }

  @Test
  public void testCreate()
  {
    final WID<Topic> topicId = WID.<Topic>generate();

    final Message testMessage = Message.builder()
                                    .id(WID.<Message>generate())
                                    .from(WID.<User>generate())
                                    .scope(MessageScope.EVERYONE)
                                    .text("Test message")
                                    .build();
    messageService.create(appId, topicId, testMessage);

    final Message message = messageService.obtain(appId, topicId, testMessage.getId());
    fail("Need to confirm message is correct");
  }

  @Test
  public void testSimpleGet()
  {

  }

  @Test
  public void testTopicGet()
  {
    final WID<Topic> topicId = WID.generate();

    final Message testMessage1 = Message.builder()
                                     .id(WID.<Message>generate())
                                     .from(WID.<User>generate())
                                     .scope(MessageScope.EVERYONE)
                                     .text("Test message 1")
                                     .build();
    messageService.create(appId, topicId, testMessage1);
    final Message testMessage2 = Message.builder()
                                     .id(WID.<Message>generate())
                                     .from(WID.<User>generate())
                                     .scope(MessageScope.EVERYONE)
                                     .text("Test message 2")
                                     .build();
    messageService.create(appId, topicId, testMessage2);

    final ImmutableList<Message> messages = messageService.obtain(appId, topicId);
    assertChatsContain(messages, testMessage1);
    assertChatsDoNotContain(messages, testMessage2);
  }

  @Test
  public void testGroupChat()
  {
    final WID<Topic> topicId = WID.generate();
    final WID<User> testId1 = WID.generate();
    final WID<User> testId2 = WID.generate();
    final WID<User> testId3 = WID.generate();

    final Message testChat = Message.builder()
                                    .id(WID.<Message>generate())
                                    .from(testId1)
                                    .scope(MessageScope.FRIENDS)
                                    .to(ImmutableSet.of(testId2, testId3))
                                    .text("Test message")
                                    .build();
    messageService.create(appId, topicId, testChat);

    final ImmutableList<Message> messages = messageService.obtain(appId, topicId, ImmutableSet.of(testId2));
    assertChatsContain(messages, testChat);
  }

  private static void assertChatsContain(@Nullable final ImmutableList<Message> chats, final Message expectedChat)
  {
    assertNotNull(chats, "Chats not supplied");
    assertFalse(chats.isEmpty(), "Chats are empty");
    int numFound = 0;
    for (final Message chat : chats)
    {
      chat.getTimestamp(); // Triggers resolution of the chat
      if (chat.equals(expectedChat))
      {
        numFound++;
      }
    }
    assertNotEquals(numFound, 0, "Failed to find matching chat");
    assertEquals(numFound, 1, "Found incorrect number of matching chats");
  }

  private static void assertChatsDoNotContain(@Nullable final ImmutableList<Message> chats, final Message expectedChat)
  {
    assertNotNull(chats, "Chats not supplied");
    assertFalse(chats.isEmpty(), "Chats are empty");
    int numFound = 0;
    for (final Message chat : chats)
    {
      if (chat.equals(expectedChat))
      {
        numFound++;
      }
    }
    assertEquals(numFound, 0, "Found incorrect number of matching chats");
  }
}
