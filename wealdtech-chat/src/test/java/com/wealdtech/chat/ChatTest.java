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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wealdtech.WID;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Tests for the chat object
 */
public class ChatTest
{
  @Test
  public void testSer() throws JsonProcessingException
  {
    final Message chat = Message.builder()
                                .id(WID.<Message>fromLong(865434565612L))
                                .from("test from")
                                .scope(MessageScope.EVERYONE)
                                .data("extkey1", "extval1")
                                .data("extkey2", "extval2")
                                .timestamp(new DateTime(1234567890L, DateTimeZone.forID("America/New_York")))
                                .topic("test topic")
                                .text("foo")
                                .build();
    final String ser = WealdMapper.getServerMapper().writeValueAsString(chat);
    assertEquals(ser,
                 "{\"_id\":\"c97feb7bec\",\"extkey1\":\"extval1\",\"extkey2\":\"extval2\",\"from\":\"test from\",\"message\":\"foo\",\"scope\":\"Everyone\",\"timestamp\":1234567890,\"topic\":\"test topic\"}");
  }

  @Test
  public void testDeser() throws IOException
  {
    final Message chat = Message.builder()
                                .id(WID.<Message>fromLong(865434565613L))
                                .from("test from")
                                .scope(MessageScope.EVERYONE)
                                .data("extkey1", "extval1")
                                .data("extkey2", "extval2")
                                .timestamp(new DateTime(1234567890L, DateTimeZone.forID("America/New_York")))
                                .topic("test topic")
                                .text("foo")
                                .build();

    final String ser =
        "{\"_id\":\"c97feb7bec\",\"message\":\"foo\",\"topic\":\"test topic\",\"timestamp\":1234567890,\"scope\":\"Everyone\",\"extkey2\":\"extval2\",\"from\":\"test from\",\"extkey1\":\"extval1\"}";

    final Message testChat = WealdMapper.getServerMapper().readValue(ser, Message.class);

    assertEquals(chat, testChat);
  }
}
