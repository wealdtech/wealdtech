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
import com.wealdtech.notifications.config.NotificationConfiguration;
import com.wealdtech.notifications.service.NotificationService;
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

  private static final String APP_ID = "message service test";

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "chat", "chat", "chat", null, null, null));

    messageService = new MessageServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                             .copy()
                                                                             .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                                                      new NotificationService(new NotificationConfiguration("com.wealdtech.notifications.providers.NotificationProviderLogImpl",
                                                                                                            "appid", "accesskey")));
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
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();

    final Message testChat = Message.builder()
                                    .appId(APP_ID)
                                    .id(WID.<Message>generate())
                                    .from(methodName)
                                    .scope(MessageScope.EVERYONE)
                                    .topic("test topic")
                                    .text("Test message")
                                    .build();
    messageService.create(testChat);

    final ImmutableList<Message> chats = messageService.obtain("test topic", methodName);
    assertChatsContain(chats, testChat);
  }

  @Test
  public void testSimpleGet()
  {

  }

  @Test
  public void testTopicGet()
  {
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
    final Message testChat1 = Message.builder()
                                     .appId(APP_ID)
                                     .id(WID.<Message>generate())
                                     .from(methodName)
                                     .scope(MessageScope.EVERYONE)
                                     .topic("test topic")
                                     .text("Test message 1")
                                     .build();
    messageService.create(testChat1);
    final Message testChat2 = Message.builder()
                                     .appId(APP_ID)
                                     .id(WID.<Message>generate())
                                     .from(methodName)
                                     .scope(MessageScope.EVERYONE)
                                     .topic("test topic 2")
                                     .text("Test message 2")
                                     .build();
    messageService.create(testChat2);

    final ImmutableList<Message> chats = messageService.obtain("test topic", methodName);
    assertChatsContain(chats, testChat1);
    assertChatsDoNotContain(chats, testChat2);
  }

  @Test
  public void testGroupChat()
  {
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();

    final WID<User> testId1 = WID.generate();
    final WID<User> testId2 = WID.generate();

    final Message testChat = Message.builder()
                                    .appId(APP_ID)
                                    .id(WID.<Message>generate())
                                    .from(methodName)
                                    .scope(MessageScope.FRIENDS)
                                    .to(ImmutableSet.of(testId1, testId2))
                                    .topic(methodName + " (topic)")
                                    .text("Test message")
                                    .build();
    messageService.create(testChat);

    final ImmutableList<Message> chats = messageService.obtain(methodName + " (topic)", methodName);
    assertChatsContain(chats, testChat);
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
