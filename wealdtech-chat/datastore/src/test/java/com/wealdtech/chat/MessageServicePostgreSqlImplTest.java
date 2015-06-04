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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.Application;
import com.wealdtech.Email;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.chat.services.MessageServicePostgreSqlImpl;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import com.wealdtech.utils.StringUtils;
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

  User user1, user2, user3 = null;
  Topic topic1, topic2 = null;

  private static final Application app = Application.builder()
                                                    .id(WID.<Application>generate())
                                                    .name("MessageServicePostgreSqlImplTest_" + StringUtils.generateRandomString(6))
                                                    .ownerId("owner")
                                                    .build();

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "chat", "chat", "chat", null, null, null));

    messageService = new MessageServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                             .copy()
                                                                             .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    messageService.createDatastore();

    user1 = User.builder()
                .id(WID.<User>generate())
                .name("Message Service Test User 1")
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .password("test")
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .build()))
                .emails(ImmutableSet.of(Email.builder().address("test1@test.com").primary(true).verified(true).build()))
                .build();
    user2 = User.builder()
                .id(WID.<User>generate())
                .name("Message Service Test User 2")
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .password("test")
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .build()))
                .emails(ImmutableSet.of(Email.builder().address("test2@test.com").primary(true).verified(true).build()))
                .build();
    user3 = User.builder()
                .id(WID.<User>generate())
                .name("Message Service Test User 3")
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .password("test")
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .build()))
                .emails(ImmutableSet.of(Email.builder().address("test3@test.com").primary(true).verified(true).build()))
                .build();
    topic1 = Topic.builder()
                  .id(WID.<Topic>generate())
                  .name("Message Service Test Topic 1")
                  .ownerIds(ImmutableSet.of(user1.getId()))
                  .participantIds(ImmutableSet.of(user1.getId()))
                  .build();
    topic2 = Topic.builder()
                  .id(WID.<Topic>generate())
                  .name("Message Service Test Topic 2")
                  .ownerIds(ImmutableSet.of(user1.getId()))
                  .participantIds(ImmutableSet.of(user1.getId()))
                  .build();
  }

  @AfterClass
  public void tearDown()
  {
    //    if (messageService != null)
    //    {
    //      messageService.destroyDatastore();
    //    }
  }

  @Test
  public void testCreate()
  {
    final Message testMessage = Message.builder()
                                       .id(WID.<Message>generate())
                                       .from(WID.<User>generate())
                                       .scope(MessageScope.EVERYONE)
                                       .text("Test message")
                                       .build();
    messageService.create(app, user1, topic1, testMessage);

    final Message message = messageService.obtain(app, user1, topic1, testMessage.getId());
    assertEquals(Message.serialize(Message.builder(testMessage).data("appid", app.getId()).data("topicid", topic1.getId()).build()),
                 Message.serialize(message));
  }

  @Test
  public void testSimpleGet()
  {

  }

  @Test
  public void testTopicGet()
  {
    final Message testMessage1 = Message.builder()
                                        .id(WID.<Message>generate())
                                        .from(user1.getId())
                                        .scope(MessageScope.EVERYONE)
                                        .text("Test message 1")
                                        .build();
    messageService.create(app, user1, topic1, testMessage1);
    final Message testMessage2 = Message.builder()
                                        .id(WID.<Message>generate())
                                        .from(user1.getId())
                                        .scope(MessageScope.EVERYONE)
                                        .text("Test message 2")
                                        .build();
    messageService.create(app, user1, topic2, testMessage2);

    final ImmutableList<Message> messages = messageService.obtain(app, user1, topic1);
    assertMessagesContain(messages,
                          Message.builder(testMessage1).data("appid", app.getId()).data("topicid", topic1.getId()).build());
    assertMessagesDoNotContain(messages,
                               Message.builder(testMessage2).data("appid", app.getId()).data("topicid", topic1.getId()).build());
    assertMessagesDoNotContain(messages,
                               Message.builder(testMessage2).data("appid", app.getId()).data("topicid", topic2.getId()).build());
  }

  @Test
  public void testGroupChat()
  {
    final Message testMessage = Message.builder()
                                       .id(WID.<Message>generate())
                                       .from(user1.getId())
                                       .scope(MessageScope.GROUP)
                                       .to(ImmutableSet.of(user2.getId(), user3.getId()))
                                       .text("Test message")
                                       .build();
    messageService.create(app, user1, topic1, testMessage);

    final ImmutableList<Message> messages = messageService.obtainTo(app, user2, topic1, null);
    assertMessagesContain(messages,
                          Message.builder(testMessage).data("appid", app.getId()).data("topicid", topic1.getId()).build());
  }

  private static void assertMessagesContain(@Nullable final ImmutableList<Message> messages, final Message expectedMessage)
  {
    assertNotNull(messages, "Messages not supplied");
    assertFalse(messages.isEmpty(), "Messages are empty");
    int numFound = 0;
    for (final Message message : messages)
    {
      if (Objects.equal(Message.serialize(message), Message.serialize(expectedMessage)))
      {
        numFound++;
      }
    }
    assertNotEquals(numFound, 0, "Failed to find matching message");
    assertEquals(numFound, 1, "Found incorrect number of matching messages");
  }

  private static void assertMessagesDoNotContain(@Nullable final ImmutableList<Message> messages, final Message expectedMessage)
  {
    assertNotNull(messages, "Message not supplied");
    assertFalse(messages.isEmpty(), "Messages are empty");
    int numFound = 0;
    for (final Message message : messages)
    {
      if (Objects.equal(Message.serialize(message), Message.serialize(expectedMessage)))
      {
        numFound++;
      }
    }
    assertEquals(numFound, 0, "Found incorrect number of matching messages");
  }
}
