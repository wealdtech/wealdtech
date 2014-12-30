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
    final Chat chat = Chat.builder()
                          .from("test from")
                          .scope(ChatScope.EVERYONE)
                          .data("extkey1", "extval1")
                          .data("extkey2", "extval2")
                          .timestamp(new DateTime(1234567890L, DateTimeZone.forID("America/New_York")))
                          .topic("test topic").message("foo").build();
    final String ser = WealdMapper.getServerMapper().writeValueAsString(chat);
    assertEquals(ser, "{\"message\":\"foo\",\"topic\":\"test topic\",\"timestamp\":\"1970-01-15T01:56:07-05:00 America/New_York\",\"scope\":\"Everyone\",\"extkey2\":\"extval2\",\"from\":\"test from\",\"extkey1\":\"extval1\"}");
  }

  @Test
  public void testDeser() throws IOException
  {
    final Chat chat = Chat.builder()
                          .from("test from")
                          .scope(ChatScope.EVERYONE)
                          .data("extkey1", "extval1")
                          .data("extkey2", "extval2")
                          .timestamp(new DateTime(1234567890L, DateTimeZone.forID("America/New_York")))
                          .topic("test topic")
                          .message("foo")
                          .build();

    final String ser = "{\"message\":\"foo\",\"topic\":\"test topic\",\"timestamp\":\"1970-01-15T01:56:07-05:00 America/New_York\",\"scope\":\"Everyone\",\"extkey2\":\"extval2\",\"from\":\"test from\",\"extkey1\":\"extval1\"}";

    final Chat testChat = WealdMapper.getServerMapper().readValue(ser, Chat.class);

    // We cannot compare directly because  the deserialized version will contain Strings rather than complex objects
    assertEquals(chat.toString(), testChat.toString());
  }
}
