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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests for the Weald object
 */
public class WObjectTest
{
  public static class TestWObject extends WObject<TestWObject>
  {
    @JsonCreator
    public TestWObject(final Map<String, Object> data)
    {
      super(data);
    }

    public static class Builder<P extends Builder<P>> extends WObject.Builder<TestWObject, P>
    {
      public Builder(){ super(); }

      public Builder(final TestWObject prior){ super(prior); }

      public TestWObject build()
      {
        return new TestWObject(data);
      }
    }

    public static Builder<?> builder(){ return new Builder(); }

    public static Builder<?> builder(final TestWObject prior){ return new Builder(prior); }
  }

  public static class TestWObject2 extends WObject<TestWObject2>
  {
    @JsonCreator
    public TestWObject2(final Map<String, Object> data)
    {
      super(data);
    }

    public static class Builder<P extends Builder<P>> extends WObject.Builder<TestWObject2, P>
    {
      public Builder(){ super(); }

      public Builder(final TestWObject2 prior){ super(prior); }

      public TestWObject2 build()
      {
        return new TestWObject2(data);
      }
    }

    public static Builder<?> builder(){ return new Builder(); }

    public static Builder<?> builder(final TestWObject2 prior){ return new Builder(prior); }
  }

  @Test
  public void testSer() throws JsonProcessingException
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .id(WID.<TestWObject>fromLong(17813481239461L))
                                            .data("test string", "test value")
                                            .data("test date", new DateTime(123456789000L, DateTimeZone.UTC))
                                            .build();
    final String testObj1Ser1 = WealdMapper.getServerMapper().writeValueAsString(testObj1);
    assertEquals(testObj1Ser1,
                 "{\"_id\":\"10338638b3a5\",\"test date\":\"1973-11-29T21:33:09.000+00:00 UTC\",\"test string\":\"test value\"}");
    final String testObj1Ser2 =
        WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(testObj1);
    assertEquals(testObj1Ser2,
                 "{\"_id\":\"10338638b3a5\",\"test date\":{\"timestamp\":123456789000},\"test string\":\"test value\"}");
  }

  @Test
  public void testDeser() throws IOException
  {
    final String testObj1Ser =
        "{\"_id\":\"10338638b3a5\",\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"}";
    final TestWObject testObj1 = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);
    assertEquals(testObj1.get("test string", String.class).orNull(), "test value");
    assertEquals(testObj1.get("test date", DateTime.class).orNull(), new DateTime(123456789000L, DateTimeZone.UTC));
  }

  @Test
  public void testSerNested() throws JsonProcessingException
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .id(WID.<TestWObject>fromLong(9876543210L))
                                            .data("test string", "test value")
                                            .data("test string 2", "test value 2")
                                            .data("test string 3", "test value 3")
                                            .data("test date", new DateTime(123456789000L, DateTimeZone.UTC))
                                            .build();
    final TestWObject testObj2 = TestWObject.builder()
                                            .id(WID.<TestWObject>fromLong(9876543211L))
                                            .data("test obj", testObj1)
                                            .data("test date", new DateTime(234567890000L, DateTimeZone.UTC))
                                            .build();
    final String testObj2Ser = WealdMapper.getServerMapper().writeValueAsString(testObj2);
    assertEquals(testObj2Ser,
                 "{\"_id\":\"24cb016eb\",\"test date\":\"1977-06-07T21:44:50.000+00:00 UTC\",\"test obj\":{\"_id\":\"24cb016ea\",\"test date\":\"1973-11-29T21:33:09.000+00:00 UTC\",\"test string\":\"test value\",\"test string 2\":\"test value 2\",\"test string 3\":\"test value 3\"}}");
  }

  @Test
  public void testDeserNested() throws IOException
  {
    final String testObj2Ser =
        "{\"_id\":\"123456abcdef\",\"test obj\":{\"_id\":\"123456abcdee\",\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"},\"test date\":\"1977-06-07T21:44:50+00:00 UTC\"}";
    final TestWObject testObj2 = WealdMapper.getServerMapper().readValue(testObj2Ser, TestWObject.class);
    final String testObj1Ser =
        "{\"_id\":\"123456abcded\",\"test date\":\"1973-11-29T21:33:09+00:00 UTC\",\"test string\":\"test value\"}";
    final TestWObject testObj1 = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);

    assertEquals(testObj2.get("test date", DateTime.class).orNull(), new DateTime(234567890000L, DateTimeZone.UTC));
    assertEquals(testObj2.get("test obj", TestWObject.class).orNull(), testObj1);
    assertEquals(testObj1.get("test date", DateTime.class).orNull(), new DateTime(123456789000L, DateTimeZone.UTC));
  }

  @Test
  public void testArrays() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .id(WID.<TestWObject>generate())
                                            .data("test date array", ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC),
                                                                                      new DateTime(234567890000L, DateTimeZone.UTC),
                                                                                      new DateTime(345678900000L,
                                                                                                   DateTimeZone.UTC)))
                                            .build();
    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);
    final WObject<?> testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, WObject.class);

    final ImmutableList<DateTime> dateTimes =
        testObj1Deser.get("test date array", new TypeReference<ImmutableList<DateTime>>() {}).get();
    assertEquals(dateTimes.get(0), new DateTime(123456789000L, DateTimeZone.UTC));
    assertEquals(dateTimes.get(1), new DateTime(234567890000L, DateTimeZone.UTC));
    assertEquals(dateTimes.get(2), new DateTime(345678900000L, DateTimeZone.UTC));
  }

  @Test
  public void testDateTimeRange() throws IOException
  {
    final Range<DateTime> testDTRange = Range.closedOpen(new DateTime(1234567890000L), new DateTime(2345678900000L));
    final TestWObject testObj1 =
        TestWObject.builder().id(WID.<TestWObject>fromLong(1234567890L)).data("test datetime range", testDTRange).build();
    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);
    final WObject<?> testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, WObject.class);

    assertEquals(testDTRange, testObj1Deser.get("test datetime range", new TypeReference<Range<DateTime>>() {}).orNull());
  }

  @Test
  public void testComplex() throws IOException
  {
    final String testObj1Ser =
        "{\"_id\":\"1\",\"code\":0,\"message\":\"ok\",\"data\":[{\"TimeZone\":{\"IsInside\":\"false\",\"AskGeoId\":20451,\"MinDistanceKm\":0.44946358,\"TimeZoneId\":\"Europe/London\",\"ShortName\":\"GMT\",\"CurrentOffsetMs\":0,\"WindowsStandardName\":\"GMT Standard Time\",\"InDstNow\":\"false\"}}]}";
    WealdMapper.getServerMapper().readValue(testObj1Ser, WObject.class);
  }

  @Test
  public void testNullValue()
  {
    final TestWObject testObj1 = TestWObject.builder().id(WID.<TestWObject>generate()).data("test null", null).build();
    assertTrue(testObj1.isEmpty());
  }

  @Test
  public void testDT()
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .id(WID.<TestWObject>generate())
                                            .data("test date", new DateTime().withZone(DateTimeZone.forID("America/New_York")))
                                            .data("test string", "test value")
                                            .build();
    assertNotNull(testObj1.toString());
  }

  @Test
  public void testBuilder() throws JsonProcessingException
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .id(WID.<TestWObject>fromLong(1234567890L))
                                            .data("test string", "test value")
                                            .data("test date", new DateTime(123456789000L, DateTimeZone.UTC))
                                            .build();
    final TestWObject testObj2 =
        TestWObject.builder(testObj1).id(WID.<TestWObject>fromLong(1234567890L)).data("test string 2", "test value 2").build();

    final String testObj2Ser1 = WealdMapper.getServerMapper().writeValueAsString(testObj2);
    assertEquals(testObj2Ser1,
                 "{\"_id\":\"499602d2\",\"test date\":\"1973-11-29T21:33:09.000+00:00 UTC\",\"test string\":\"test value\",\"test string 2\":\"test value 2\"}");
    final String testObj2Ser2 =
        WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(testObj2);
    assertEquals(testObj2Ser2,
                 "{\"_id\":\"499602d2\",\"test date\":{\"timestamp\":123456789000},\"test string\":\"test value\",\"test string 2\":\"test value 2\"}");
  }

  // Ensure that WObject obeys the type passed in on get
  @Test
  public void testClassGeneric() throws IOException
  {
    final TestWObject testObj1 =
        TestWObject.builder().id(WID.<TestWObject>fromLong(1234567890L)).data("test array", ImmutableList.of(1, 2, 3)).build();
    final Optional<ImmutableSet> unspecifiedSet = testObj1.get("test array", ImmutableSet.class);
    assertTrue(unspecifiedSet.isPresent());
    assertEquals(unspecifiedSet.get().size(), 3);
    // Because we didn't specify a type the elements should be Integers
    assertEquals(unspecifiedSet.get().iterator().next().getClass(), Integer.class);
  }

  // Ensure that internal values are not considered when check for equality
  @Test
  public void testIgnoreInternals() throws IOException
  {
    final TestWObject testObj1 =
        TestWObject.builder().id(WID.<TestWObject>generate()).data(ImmutableMap.of("key", "val", "_version", 1)).build();
    final TestWObject testObj2 =
        TestWObject.builder().id(WID.<TestWObject>generate()).data("key", "val").data("_version", 2).build();
    assertEquals(testObj1, testObj2);
  }

  // Ensure that null entries in the builder are handled correctly
  @Test
  public void testNullInBuilder() throws IOException
  {
    final TestWObject testObj1 =
        TestWObject.builder().id(WID.<TestWObject>generate()).data("key", "val").data("key2", "val2").build();
    assertTrue(testObj1.exists("key2"));
    final TestWObject testObj2 = TestWObject.builder(testObj1).id(WID.<TestWObject>generate()).data("key2", null).build();
    assertFalse(testObj2.exists("key2"));
  }

  @Test
  public void testRepeatedGets() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder().data("dt", "1234567890000").data("wid", "1").build();
    final WID<String> wid = testObj1.get("wid", new TypeReference<WID<String>>() {}).get();
    final WID<String> wid2 = testObj1.get("wid", new TypeReference<WID<String>>() {}).get();
    final WID<DateTime> wid3 = testObj1.get("wid", new TypeReference<WID<DateTime>>() {}).get();
    final DateTime dt1 = testObj1.get("dt", DateTime.class).get();
    final DateTime dt2 = testObj1.get("dt", DateTime.class).get();
  }

  @Test
  public void testOrdering() throws IOException
  {
    final TestWObject2 subObj1 =
        TestWObject2.builder().id(WID.<TestWObject2>generate()).data("c", "1").data("b", "2").data("a", "3").build();
    final TestWObject testObj1 = TestWObject.builder().data("dt", "1234567890000").data("wid", "1").data("obj", subObj1).build();

    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);

    final TestWObject testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);

    assertTrue(Objects.equal(testObj1Deser, testObj1));
  }

  @Test
  public void testDeepOrdering() throws IOException
  {
    final TestWObject2 subObj1 =
        TestWObject2.builder().id(WID.<TestWObject2>generate()).data("c", "1").data("b", "2").data("a", "3").build();
    final TestWObject2 subObj2 =
        TestWObject2.builder().id(WID.<TestWObject2>generate()).data("foo", "1").data("bar", "2").data("baz", "3").build();
    final TestWObject2 subObj3 =
        TestWObject2.builder().id(WID.<TestWObject2>generate()).data("Do", "1").data("Re", "2").data("Mi", "3").build();
    final List<TestWObject2> subObjs = Lists.newArrayList(subObj1, subObj2, subObj3);
    final TestWObject testObj1 = TestWObject.builder().data("dt", "1234567890000").data("wid", "1").data("subs", subObjs).build();

    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);

    final TestWObject testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);

    assertTrue(Objects.equal(testObj1Deser, testObj1));
  }

  @Test
  public void testConsistentHash() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder().data("dt", ImmutableMap.of("timestamp", 123456789000L)).build();
    final int preFetchHashCode = testObj1.hashCode();
    testObj1.get("dt", DateTime.class);
    // internal structure of WObject will now contain the date time but the hash should remain the same
    final int postFetchHashCode = testObj1.hashCode();
    assertEquals(preFetchHashCode, postFetchHashCode);

    // Also ensure that WObject built with real object from the start provides the same hash
    final TestWObject testObj2 = TestWObject.builder().data("dt", new DateTime(123456789000L, DateTimeZone.UTC)).build();
    final int realObjectHashCode = testObj2.hashCode();
    assertEquals(preFetchHashCode, realObjectHashCode);
  }

  @Test
  public void testAbsent()
  {
    final TestWObject testObj1 = TestWObject.builder().data("here", 1).build();
    assertFalse(testObj1.get("nothere", String.class).isPresent());
    assertFalse(testObj1.get("nothere", new TypeReference<ImmutableList<String>>() {}).isPresent());
  }

  @Test
  public void testAllData()
  {
    final WID<TestWObject> wid = WID.generate();
    final TestWObject testObj1 = TestWObject.builder().id(wid).data("int", 1).build();
    assertEquals(testObj1.getId(), wid);
    assertFalse(testObj1.getData().containsKey("_id"));
    assertTrue(testObj1.getAllData().containsKey("_id"));
  }

  @Test
  public void testGeneric()
  {
    final TestWObject testObj1 = TestWObject.builder().data("one", 1).data("two", "{\"a\":2}").build();
    assertFalse(testObj1.get("one").isPresent());
    assertTrue(testObj1.get("two").isPresent());
  }

  // We can embed a map of any sort in to a WObject; ensure that it doesn't cause problems
  @Test
  public void testNonStringKeys()
  {
    final Map<WID, String> map = ImmutableMap.<WID, String>of(WID.generate(), "1", WID.generate(), "2");
    final TestWObject testObj1 = TestWObject.builder().data("one", "1").data("map", map).build();
    testObj1.getData();
    testObj1.getAllData();
    testObj1.hashCode();
    testObj1.toString();
  }

  @Test
  public void testBoolean()
  {
    final TestWObject testObj1 = TestWObject.builder().data("one", true).data("two", "true").build();
    assertTrue(testObj1.get("one", Boolean.class).get());
    assertTrue(testObj1.get("two", Boolean.class).get());
  }

  @Test
  public void testLocalDateTime()
  {
    final TestWObject testObj1 =
        TestWObject.builder().data("one", new LocalDateTime(1234567890000L)).data("two", 1234567890000L).build();
    assertEquals(testObj1.get("one", LocalDateTime.class).get(), new LocalDateTime(1234567890000L));
    assertEquals(testObj1.get("two", LocalDateTime.class).get(), new LocalDateTime(1234567890000L));
  }

  @Test
  public void testSimpleTriVal() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder().data("one", TriVal.of("One")).build();

    final String testObj1Ser = WealdMapper.getServerMapper().writeValueAsString(testObj1);

    final TestWObject testObj1Deser = WealdMapper.getServerMapper().readValue(testObj1Ser, TestWObject.class);

    assertTrue(Objects.equal(testObj1Deser, testObj1));
  }

  @Test
  public void testListTriVal() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder()
                                            .data("one", TriVal.of(ImmutableList.of("One", "One1")))
                                            .data("two", TriVal.clear())
                                            .data("three", TriVal.absent())
                                            .build();

    final String testObj1Ser = TestWObject.serialize(testObj1);
    final TestWObject testObj1Deser = TestWObject.deserialize(testObj1Ser, TestWObject.class);
    assertNotNull(testObj1Deser);

    assertEquals(testObj1Deser.get("three", TriVal.class), Optional.absent());
    assertEquals(testObj1Deser.get("two", TriVal.class).get(), TriVal.clear());
  }

  @Test
  public void testEmptyString() throws IOException
  {
    final String testObj1Ser = "{\"one\":\"\"}";
    final TestWObject testObj1Deser = TestWObject.deserialize(testObj1Ser, TestWObject.class);
    assertNotNull(testObj1Deser);
    assertEquals(testObj1Deser.get("one", new TypeReference<String>() {}).get(), "");
  }

  @Test
  public void testTriValString() throws IOException
  {
    final String testObj1Ser = "{\"one\":\"test\"}";
    final TestWObject testObj1Deser = TestWObject.deserialize(testObj1Ser, TestWObject.class);
    assertNotNull(testObj1Deser);

    assertEquals(testObj1Deser.get("one", new TypeReference<TriVal<String>>() {}).get(), TriVal.of("test"));
  }

  @Test
  public void testTriValClear() throws IOException
  {
    final TestWObject testObj1 = TestWObject.builder().data("clear", TriVal.clear()).build();

    final String testObj1Ser = TestWObject.serialize(testObj1);
    assertEquals(testObj1Ser, "{\"clear\":\"__\"}");
    final TestWObject testObj1Deser = TestWObject.deserialize(testObj1Ser, TestWObject.class);
    assertNotNull(testObj1Deser);

    assertEquals(testObj1Deser.get("clear", new TypeReference<TriVal<ImmutableList<DateTime>>>() {}).get(), TriVal.clear());
  }

  @Test
  public void testList() throws IOException
  {
    final ImmutableList<DateTime> list =
        ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                         new DateTime(3456789000000L, DateTimeZone.UTC));
    final GenericWObject testObj1 = GenericWObject.builder().data("list", list).build();
    assertEquals(((List)testObj1.getAllData().get("list")).get(0).getClass(), DateTime.class);
  }

  @Test
  public void testListRoundtrip() throws IOException
  {
    final TypeReference<List<DateTime>> listTypeRef = new TypeReference<List<DateTime>>(){};

    final ImmutableList<DateTime> list =
        ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                         new DateTime(3456789000000L, DateTimeZone.UTC));
    GenericWObject testObj1 = GenericWObject.builder().data("list", list).build();
    final List<DateTime> res = testObj1.get("list", listTypeRef).get();
    assertTrue(ImmutableList.class.isAssignableFrom(res.getClass()));
    assertEquals(res.get(0).getClass(), DateTime.class);

    final GenericWObject testObj2 = GenericWObject.builder(testObj1).build();
    final List<DateTime> res2 = testObj2.get("list", listTypeRef).get();
    assertTrue(ImmutableList.class.isAssignableFrom(res2.getClass()));
    assertEquals(res2.get(0).getClass(), DateTime.class);

    testObj1 = GenericWObject.deserialize(GenericWObject.serialize(testObj1), GenericWObject.class);
    final List<DateTime> res3 = testObj1.get("list", listTypeRef).get();
    assertEquals(res3.get(0).getClass(), DateTime.class);
  }

  @Test
  public void testSetRoundtrip() throws IOException
  {
    final TypeReference<Set<DateTime>> setTypeRef = new TypeReference<Set<DateTime>>(){};

    final ImmutableSet<DateTime> set =
        ImmutableSet.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                         new DateTime(3456789000000L, DateTimeZone.UTC));
    GenericWObject testObj1 = GenericWObject.builder().data("set", set).build();
    final Set<DateTime> res = testObj1.get("set", setTypeRef).get();
    assertTrue(ImmutableSet.class.isAssignableFrom(res.getClass()));
    assertEquals(res.iterator().next().getClass(), DateTime.class);

    final GenericWObject testObj2 = GenericWObject.builder(testObj1).build();
    final Set<DateTime> res2 = testObj2.get("set", setTypeRef).get();
    assertTrue(ImmutableSet.class.isAssignableFrom(res2.getClass()));
    assertEquals(res2.iterator().next().getClass(), DateTime.class);

    testObj1 = GenericWObject.deserialize(GenericWObject.serialize(testObj1), GenericWObject.class);
    final Set<DateTime> res3 = testObj1.get("set", setTypeRef).get();
    assertEquals(res3.iterator().next().getClass(), DateTime.class);
  }

  @Test
  public void testTriValList() throws IOException
  {
    final TriVal<ImmutableList<DateTime>> list =
        TriVal.of(ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                         new DateTime(3456789000000L, DateTimeZone.UTC)));
    GenericWObject testObj1 = GenericWObject.builder().data("list", list).build();
    final TypeReference<TriVal<List<DateTime>>> listTypeRef = new TypeReference<TriVal<List<DateTime>>>(){};
    final TriVal<List<DateTime>> res = testObj1.get("list", listTypeRef).get();
    assertEquals(res.get().get(0).getClass(), DateTime.class);
  }

  @Test
  public void testTriValListRoundtrip() throws IOException
  {
    final TriVal<ImmutableList<DateTime>> list =
        TriVal.of(ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                                   new DateTime(3456789000000L, DateTimeZone.UTC)));
    GenericWObject testObj1 = GenericWObject.builder().data("list", list).build();
    testObj1 = GenericWObject.deserialize(GenericWObject.serialize(testObj1), GenericWObject.class);
    final TypeReference<TriVal<List<DateTime>>> listTypeRef = new TypeReference<TriVal<List<DateTime>>>(){};
    final TriVal<List<DateTime>> res = testObj1.get("list", listTypeRef).get();
    assertEquals(res.get().get(0).getClass(), DateTime.class);
  }

  @Test
  public void testListOrdering() throws IOException
  {
    final ImmutableList<DateTime> list1 =
        ImmutableList.of(new DateTime(123456789000L, DateTimeZone.UTC), new DateTime(234567890000L, DateTimeZone.UTC),
                         new DateTime(3456789000000L, DateTimeZone.UTC));
    final GenericWObject testObj1 = GenericWObject.builder().data("list", list1).build();
    assertEquals(testObj1.get("list", new TypeReference<ImmutableList<DateTime>>() {}).get().get(0),
                 new DateTime(123456789000L, DateTimeZone.UTC));

    final ImmutableList<DateTime> list2 =
        ImmutableList.of(new DateTime(3456789000000L, DateTimeZone.UTC), new DateTime(123456789000L, DateTimeZone.UTC),
                         new DateTime(234567890000L, DateTimeZone.UTC));
    final GenericWObject testObj2 = GenericWObject.builder().data("list", list2).build();
    assertEquals(testObj2.get("list", new TypeReference<ImmutableList<DateTime>>() {}).get().get(0),
                 new DateTime(3456789000000L, DateTimeZone.UTC));
  }
}
