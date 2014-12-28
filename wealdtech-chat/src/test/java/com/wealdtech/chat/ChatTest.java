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
import com.google.common.collect.Maps;
import com.wealdtech.jackson.WealdMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Tests for the chat object
 */
public class ChatTest
{
  @Test
  public void testSer() throws JsonProcessingException
  {
    final Map<String, Object> extensions = Maps.newHashMap();
    extensions.put("extkey1", "extval1");
    extensions.put("extkey2", "extval2");
    final Chat chat = Chat.builder()
                          .from("test from")
                          .scope(ChatScope.EVERYONE)
                          .extensions(extensions)
                          .timestamp(1234567890L)
                          .topic("test topic")
                          .message("foo")
                          .build();
    final String ser = WealdMapper.getServerMapper().writeValueAsString(chat);
    assertEquals(ser, "{\"from\":\"test from\",\"scope\":\"EVERYONE\",\"timestamp\":1234567890,\"topic\":\"test topic\",\"message\":\"foo\",\"extkey2\":\"extval2\",\"extkey1\":\"extval1\"}");
  }

  @Test
  public void testDeser() throws IOException
  {
    final Map<String, Object> extensions = Maps.newHashMap();
    extensions.put("extkey1", "extval1");
    extensions.put("extkey2", "extval2");
    final Chat chat = Chat.builder()
                          .from("test from")
                          .scope(ChatScope.EVERYONE)
                          .extensions(extensions)
                          .timestamp(1234567890L)
                          .topic("test topic")
                          .message("foo")
                          .build();

    final String ser = "{\"from\":\"test from\",\"scope\":\"EVERYONE\",\"timestamp\":1234567890,\"topic\":\"test topic\",\"message\":\"foo\",\"extkey2\":\"extval2\",\"extkey1\":\"extval1\"}";

    final Chat testChat = WealdMapper.getServerMapper().readValue(ser, Chat.class);

    assertEquals(chat, testChat);
  }
}
