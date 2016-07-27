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
import com.wealdtech.WID;
import com.wealdtech.services.chat.ChatClient;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Message-based tests for the Wealdtech chat daemon
 */
public class MessageTest
{
  // Ensure we can send and retrieve a simple message
  @Test
  public void testSimpleMessage()
  {
    final ChatDTest test = new ChatDTest();
    test.setUp();
    final ChatClient user1ChatClient =
        new ChatClient(test.application.getId().toString(), test.user1.getEmails().iterator().next().getAddress(), "test");
//    final ChatClient user1ChatClient =
//        new ChatClient(ChatDTest.application.getId().toString(), ChatDTest.user1.getEmails().iterator().next().getAddress(),
//                       "test");
    final WID<Topic> topicId = WID.generate();

    Message testMessage = null;
    try
    {
      testMessage = Message.builder()
                           .id(WID.<Message>generate())
                           .fromId(ChatDTest.user1.getId())
                           .scope(MessageScope.INDIVIDUAL)
                           .toIds(ImmutableSet.of(ChatDTest.user2.getId()))
                           .timestamp(new LocalDateTime())
                           .text("Simple message")
                           .build();
      user1ChatClient.createMessage(topicId, testMessage);

      // Ensure that we can obtain the message
      final Message message = user1ChatClient.obtainMessage(topicId, testMessage.getId());
      // Serialize before comparison to avoid issues with comparing complex objects with strings
      assertEquals(Message.serialize(Message.builder(testMessage)
                                            .data("appid", ChatDTest.application.getId())
                                            .data("topicid", topicId)
                                            .build()), Message.serialize(message));
    }
    finally
    {
      // Removals not yet implements
//      if (testMessage != null)
//      {
//        user1ChatClient.removeTopic(topicId);
//      }
    }
  }
}
