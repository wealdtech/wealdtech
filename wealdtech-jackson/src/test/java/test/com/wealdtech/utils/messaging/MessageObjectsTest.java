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

package test.com.wealdtech.utils.messaging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.messaging.MessageObjects;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class MessageObjectsTest
{
  private final ObjectMapper mapper = WealdMapper.getMapper();

  @BeforeClass
  public void setUp()
  {
    mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testSer() throws Exception
  {
    final MessageObjects<TestData> testMo = new MessageObjects<>(12345L, new TestData("Prior string", 0), new TestData("Current string", 1));
    final String ser = mapper.writeValueAsString(testMo);
    assertEquals(ser,  "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"prior\":{\"mystr\":\"Prior string\",\"myint\":0},\"current\":{\"mystr\":\"Current string\",\"myint\":1}}");
  }

  @Test
  public void testSerNullPrior() throws Exception
  {
    final MessageObjects<TestData> testMo = new MessageObjects<>(12345L, null, new TestData("Current string", 1));
    final String ser = mapper.writeValueAsString(testMo);
    assertEquals(ser,  "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"current\":{\"mystr\":\"Current string\",\"myint\":1}}");
  }

  @Test
  public void testSerNullCurrent() throws Exception
  {
    final MessageObjects<TestData> testMo = new MessageObjects<>(12345L, new TestData("Prior string", 0), null);
    final String ser = mapper.writeValueAsString(testMo);
    assertEquals(ser,  "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"prior\":{\"mystr\":\"Prior string\",\"myint\":0}}");
  }

  @Test
  public void testDeser() throws Exception
  {
    final String ser = "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"prior\":{\"mystr\":\"Prior string\",\"myint\":0},\"current\":{\"mystr\":\"Current string\",\"myint\":1}}";
    final TypeReference<MessageObjects<TestData>> type = new TypeReference<MessageObjects<TestData>>(){};
    final MessageObjects<TestData> mo = mapper.readValue(ser, type);
    assertEquals(mapper.writeValueAsString(mo), ser);
  }

  @Test
  public void testDeserNullPrior() throws Exception
  {
    final String ser = "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"current\":{\"mystr\":\"Current string\",\"myint\":1}}";
    final TypeReference<MessageObjects<TestData>> type = new TypeReference<MessageObjects<TestData>>(){};
    final MessageObjects<TestData> mo = mapper.readValue(ser, type);
    assertEquals(mapper.writeValueAsString(mo), ser);
  }

  @Test
  public void testDeserNullCurrent() throws Exception
  {
    final String ser = "{\"userid\":12345,\"_type\":\"test.com.wealdtech.utils.messaging.MessageObjectsTest$TestData\",\"prior\":{\"mystr\":\"Prior string\",\"myint\":0}}";
    final TypeReference<MessageObjects<TestData>> type = new TypeReference<MessageObjects<TestData>>(){};
    final MessageObjects<TestData> mo = mapper.readValue(ser, type);
    assertEquals(mapper.writeValueAsString(mo), ser);
  }

  @Test
  public void testDeserInvalid() throws Exception
  {
    // Missing type
    final String ser = "{\"prior\":{\"mystr\":\"Prior string\",\"myint\":0},\"current\":{\"mystr\":\"Current string\",\"myint\":1}}";
    final TypeReference<MessageObjects<TestData>> type = new TypeReference<MessageObjects<TestData>>(){};
    try
    {
      mapper.readValue(ser, type);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // Unknown type
    final String ser2 = "{\"_type\":\"com.wealdtech.utils.messaging.UnknownClass\",\"prior\":{\"mystr\":\"Prior string\",\"myint\":0},\"current\":{\"mystr\":\"Current string\",\"myint\":1}}";
    try
    {
       mapper.readValue(ser2, type);
       // Should not reach here
       fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // Both null
    final String ser3 = "{\"_type\":\"com.wealdtech.utils.messaging.MessageObjectsTest$TestData\"}";
    try
    {
       mapper.readValue(ser3, type);
       // Should not reach here
       fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }
  }

  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  public static final class TestData
  {
    private final String myStr;
    private final int myInt;

    @JsonCreator
    public TestData(final @JsonProperty("mystr") String myStr, final @JsonProperty("myint") int myInt)
    {
      this.myStr = myStr;
      this.myInt = myInt;
    }

    public String getMyStr()
    {
      return this.myStr;
    }

    public int getMyInt()
    {
      return this.myInt;
    }
  }}
