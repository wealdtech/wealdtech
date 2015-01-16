/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.google.common.collect.ImmutableList;
import com.wealdtech.chat.services.ChatServicePostgreSqlImpl;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nullable;

import static org.testng.Assert.*;

/**
 * Test for the PostgreSQL implementation of the chat service
 */
public class ChatServicePostgreSqlImplTest
{
  ChatServicePostgreSqlImpl service;
  PostgreSqlRepository repo;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "chat", "chat", "chat", null, null, null));
    service = new ChatServicePostgreSqlImpl(repository);
    service.createDatastore();
  }

  @AfterClass
  public void tearDown()
  {
    if (service != null)
    {
      service.destroyDatastore();
    }
  }

  @Test
  public void testAdd()
  {
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();

    final Chat testChat = Chat.builder()
                              .from(methodName)
                              .scope(ChatScope.EVERYONE)
                              .topic("test topic")
                              .message("Test message")
                              .build();
    service.add(testChat);

    final ImmutableList<Chat> chats = service.getChats(methodName, "test topic");
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
    final Chat testChat1 = Chat.builder()
                               .from(methodName)
                               .scope(ChatScope.EVERYONE)
                               .topic("test topic")
                               .message("Test message 1")
                               .build();
    service.add(testChat1);
    final Chat testChat2 = Chat.builder()
                               .from(methodName)
                               .scope(ChatScope.EVERYONE)
                               .topic("test topic 2")
                               .message("Test message 2")
                               .build();
    service.add(testChat2);

    final ImmutableList<Chat> chats = service.getChats(methodName, "test topic");
    assertChatsContain(chats, testChat1);
    assertChatsDoNotContain(chats, testChat2);
  }

  private static void assertChatsContain(@Nullable final ImmutableList<Chat> chats, final Chat expectedChat)
  {
    assertNotNull(chats, "Chats not supplied");
    assertFalse(chats.isEmpty(), "Chats are empty");
    int numFound = 0;
    for (final Chat chat : chats)
    {
      if (chat.equals(expectedChat))
      {
        numFound++;
      }
    }
    assertNotEquals(numFound, 0, "Failed to find matching chat");
    assertEquals(numFound, 1, "Found incorrect number of matching chats");
  }

  private static void assertChatsDoNotContain(@Nullable final ImmutableList<Chat> chats, final Chat expectedChat)
  {
    assertNotNull(chats, "Chats not supplied");
    assertFalse(chats.isEmpty(), "Chats are empty");
    int numFound = 0;
    for (final Chat chat : chats)
    {
      if (chat.equals(expectedChat))
      {
        numFound++;
      }
    }
    assertEquals(numFound, 0, "Found incorrect number of matching chats");
  }
}
