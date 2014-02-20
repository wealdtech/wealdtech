/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.utils.messaging;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wealdtech.DataError;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.messaging.MessageItem.Type;

import static org.testng.Assert.*;

public class MessageItemTest
{
  private final transient ObjectMapper mapper = WealdMapper.getMapper();

  @BeforeClass
  public void setUp()
  {
    mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testModel() throws Exception
  {
    final MessageItem testMessageItem1 = new MessageItem(Type.PUBLISH, "My.Destination", null);
    testMessageItem1.getMsgType();
    testMessageItem1.getDestination();
    testMessageItem1.getObjects();
  }

  @Test
  public void testType() throws Exception
  {
    final Type testType = Type.fromString("publish");
    assertEquals(testType, Type.PUBLISH);

    try
    {
      Type.fromString(null);
      fail("Created null type");
    }
    catch (DataError.Missing de)
    {
      // Good
    }

    try
    {
      Type.fromString("Invalid");
      fail("Created invalid type");
    }
    catch (DataError.Bad de)
    {
      // Good
    }

    assertEquals(Type.QUEUE.toString(), "Queue");
  }
}
