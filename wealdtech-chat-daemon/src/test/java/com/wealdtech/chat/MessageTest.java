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

import com.google.common.collect.ImmutableSet;
import com.wealdtech.*;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.services.chat.ChatClient;
import com.wealdtech.utils.StringUtils;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Message-based tests for the Wealdtech chat daemon
 */
public class MessageTest
{
  User u1, u2;
  WID<Application> appId;

  @BeforeClass
  public void setUp()
  {
    final String salt = StringUtils.generateRandomString(6);
    u1 = User.builder()
             .id(WID.<User>generate())
             .name("MessageTest1")
             .emails(ImmutableSet.of(Email.builder()
                                          .address("messagetest1" + salt + "@test.wealdtech.com")
                                          .primary(true)
                                          .verified(true)
                                          .build()))
             .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                .scope(AuthorisationScope.FULL)
                                                                                .password("test")
                                                                                .build()))
             .deviceRegistrations(ImmutableSet.of(DeviceRegistration.builder().type(DeviceType.ANDROID).deviceId("foo").build()))
             .build();
    u2 = User.builder()
             .id(WID.<User>generate())
             .name("MessageTest1")
             .emails(ImmutableSet.of(Email.builder()
                                          .address("messagetest2" + salt + "@test.wealdtech.com")
                                          .primary(true)
                                          .verified(true)
                                          .build()))
             .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                .scope(AuthorisationScope.FULL)
                                                                                .password("test")
                                                                                .build()))
             .deviceRegistrations(ImmutableSet.of(DeviceRegistration.builder().type(DeviceType.ANDROID).deviceId("foo").build()))
             .build();
    appId = WID.generate();
  }

  @AfterClass
  public void tearDown()
  {

  }

  // Ensure we can send and retrieve a simple message
  @Test
  public void testSimpleMessage()
  {
    final WID<Topic> topicId = WID.generate();

    Message testMessage = null;
    try
    {
      testMessage = Message.builder()
                           .id(WID.<Message>generate())
                           .from(u1.getId())
                           .scope(MessageScope.INDIVIDUAL)
                           .to(ImmutableSet.of(u2.getId()))
                           .timestamp(new DateTime())
                           .text("Simple message")
                           .build();
      ChatClient.getInstance().createMessage(appId, topicId, testMessage);

      // Ensure that we can obtain the message
      final Message message = ChatClient.getInstance().obtainMessage(appId, topicId, testMessage.getId());
      // Serialize before comparison to avoid issues with comparing complex objects with strings
      assertEquals(Message.serialize(Message.builder(testMessage).data("appid", appId).data("topicid", topicId).build()),
                   Message.serialize(message));
    }
    finally
    {
      if (testMessage != null)
      {
//        ChatClient.getInstance().removeMessage(appId, topicId, testMessage.getId());
      }
    }
  }
}
