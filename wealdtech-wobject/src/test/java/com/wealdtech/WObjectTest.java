/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Tests for the Weald object
 */
public class WObjectTest
{
  public static class TestWObject extends WObject<TestWObject>
  {
    @JsonCreator
    public TestWObject(final ImmutableMap<String, Object> data)
    {
      super(data);
    }

    public static class Builder extends WObject.Builder<TestWObject.Builder>
    {
      public TestWObject build()
      {
        return new TestWObject(data.build());
      }
    }
    public static Builder builder() { return new Builder(); }
  };

  @Test
  public void testSer() throws JsonProcessingException
  {
    final TestWObject testObj1 = TestWObject.builder().data("test string", "test value").data("test date", new DateTime(123456789000L, DateTimeZone.UTC)).build();
    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);
    assertEquals(testObj1Ser, "{\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"}");
  }

  @Test
  public void testDeser() throws IOException
  {
    final String testObj1Ser = "{\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"}";
    final TestWObject testObj1 = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);
    assertEquals(testObj1.get("test string", String.class), "test value");
    assertEquals(testObj1.get("test date", DateTime.class), new DateTime(123456789000L, DateTimeZone.UTC));
  }

  @Test
  public void testSerNested() throws JsonProcessingException
  {
    final TestWObject testObj1 = TestWObject.builder().data("test string", "test value").data("test date", new DateTime(123456789000L, DateTimeZone.UTC)).build();
    final TestWObject testObj2 = TestWObject.builder().data("test obj", testObj1).data("test date", new DateTime(234567890000L, DateTimeZone.UTC)).build();
    final String testObj2Ser = WealdMapper.getServerMapper().writeValueAsString(testObj2);
    assertEquals(testObj2Ser, "{\"test obj\":{\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"},\"test date\":\"1977-06-07T21:44:50+00:00 UTC\"}");
  }

  @Test
  public void testDeserNested() throws IOException
  {
    final String testObj2Ser =  "{\"test obj\":{\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"},\"test date\":\"1977-06-07T21:44:50+00:00 UTC\"}";
    final TestWObject testObj2 = WealdMapper.getServerMapper().readValue(testObj2Ser, TestWObject.class);
    final String testObj1Ser =  "{\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"}";
    final TestWObject testObj1 = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);

    assertEquals(testObj2.get("test date", DateTime.class), new DateTime(234567890000L, DateTimeZone.UTC));
    assertEquals(testObj2.get("test obj", TestWObject.class), testObj1);
    assertEquals(testObj1.get("test date", DateTime.class), new DateTime(123456789000L, DateTimeZone.UTC));
  }

  @Test
  public void testArrays() throws JsonProcessingException, IOException
  {
    final TestWObject testObj1 = TestWObject.builder().data("test date array", ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC),
                                                                                        new DateTime(234567890000L, DateTimeZone.UTC),
                                                                                        new DateTime(345678900000L, DateTimeZone.UTC))).build();
    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);
    System.err.println(testObj1Ser);
    final WObject<?> testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, WObject.class);

    final ImmutableList<DateTime> dateTimes = testObj1Deser.get("test date array", new TypeReference<ImmutableList<DateTime>>() {}).get();
    assertEquals(dateTimes.get(0), new DateTime(123456789000L, DateTimeZone.UTC));
    assertEquals(dateTimes.get(1), new DateTime(234567890000L, DateTimeZone.UTC));
    assertEquals(dateTimes.get(2), new DateTime(345678900000L, DateTimeZone.UTC));
  }

  @Test
  public void testComplex() throws JsonProcessingException, IOException
  {
    final String testObj1Ser = "{\"code\":0,\"message\":\"ok\",\"data\":[{\"TimeZone\":{\"IsInside\":\"false\",\"AskGeoId\":20451,\"MinDistanceKm\":0.44946358,\"TimeZoneId\":\"Europe/London\",\"ShortName\":\"GMT\",\"CurrentOffsetMs\":0,\"WindowsStandardName\":\"GMT Standard Time\",\"InDstNow\":\"false\"}}]}";
    final WObject<?> testObj1 = WealdMapper.getServerMapper().readValue(testObj1Ser, WObject.class);
  }

}
