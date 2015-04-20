/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Range;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;

public class MiscModuleTest
{
  private final transient ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper().copy();

  @BeforeClass
  public void setUp()
  {
    this.mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testDeserInetSocketAddress() throws Exception
  {
    final String ser = "\"www.wealdtech.com:12345\"";
    final InetSocketAddress deser = this.mapper.readValue(ser, InetSocketAddress.class);
    assertEquals(deser, new InetSocketAddress("www.wealdtech.com", 12345));
  }


  @Test
  public void testDeserRange() throws Exception
  {
    final String ser = "\"[2013-01-02T03:00:00Z Europe/London,2013-01-02T04:00:00Z Europe/London)\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser,  Range.closedOpen(new DateTime(2013, 1, 2, 3, 0, 0).withZoneRetainFields(DateTimeZone.forID("Europe/London")),
                                          new DateTime(2013, 1, 2, 4, 0, 0).withZoneRetainFields(DateTimeZone.forID("Europe/London"))));
  }

  @Test
  public void testDeserRange2() throws Exception
  {
    final String ser = "\"[2014-06-01T00:00:00.000+01:00‥2016-10-01T00:00:00.000+01:00)\"";
    final Range<DateTime> deser = WealdMapper.getMapper().readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser,  Range.closedOpen(new DateTime(2014, 6, 1, 0, 0, 0).withZoneRetainFields(DateTimeZone.forID("Europe/London")),
                                          new DateTime(2016, 10, 1, 0, 0, 0).withZoneRetainFields(DateTimeZone.forID("Europe/London"))));
  }

  @Test
  public void testDeserUnboundedRange() throws Exception
  {
    final String ser = "\"[2013-01-02T03:00:00+13:45 Pacific/Chatham,+∞)\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser, Range.atLeast(new DateTime(2013, 1, 2, 3, 0, 0).withZoneRetainFields(DateTimeZone.forID("Pacific/Chatham"))));
  }

  @Test
  public void testDeserInfiniteRange() throws Exception
  {
    final String ser = "\"(-∞,+∞)\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser, Range.<DateTime>all());
  }

  @Test
  public void testDeserLazyRange() throws Exception
  {
    final String ser = "\"2014-05-03T03:00:00Z Europe/London,2014-05-04T04:00:00Z Europe/London\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser,  Range.closedOpen(new DateTime(2014, 5, 3, 3, 0, 0, 0, DateTimeZone.UTC),
                                          new DateTime(2014, 5, 4, 4, 0, 0, 0, DateTimeZone.UTC)));
  }

  @Test
  public void testDeserLazyRangeNoTz() throws Exception
  {
    final String ser = "\"2014-05-03T04:00:00+01:00,2014-05-03T06:00:00+01:00\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser,  Range.closedOpen(new DateTime(2014, 5, 3, 3, 0, 0, 0, DateTimeZone.UTC),
                                          new DateTime(2014, 5, 3, 5, 0, 0, 0, DateTimeZone.UTC)));
  }

  @Test
  public void testDeserGuavaRange() throws Exception
  {
    final String ser = "\"[2014-05-05T03:00:00.000Z‥2014-05-05T04:00:00.000Z)\"";
    final Range<DateTime> deser = this.mapper.readValue(ser, new TypeReference<Range<DateTime>>(){});
    assertEquals(deser,  Range.closedOpen(new DateTime(2014, 5, 5, 3, 0, 0, 0, DateTimeZone.UTC),
                                          new DateTime(2014, 5, 5, 4, 0, 0, 0, DateTimeZone.UTC)));
  }

  @Test
  public void testSerInetSocketAddress() throws Exception
  {
    final InetSocketAddress addr = new InetSocketAddress("www.wealdtech.com", 23456);
    final String ser = this.mapper.writeValueAsString(addr);
    assertEquals(ser, "\"www.wealdtech.com:23456\"");
  }

  @Test
  public void testSerRange() throws Exception
  {
    final Range<DateTime> range = Range.closedOpen(new DateTime(2013, 1, 2, 3, 0, 0).withZoneRetainFields(DateTimeZone.forID("Europe/London")),
                                                   new DateTime(2013, 1, 2, 4, 0, 0).withZoneRetainFields(DateTimeZone.forID("America/New_York")));
    final String ser = this.mapper.writeValueAsString(range);
    assertEquals(ser, "\"[2013-01-02T03:00:00+00:00 Europe/London,2013-01-02T04:00:00-05:00 America/New_York)\"");
  }

  @Test
  public void testSerUnboundedRange() throws Exception
  {
    final Range<DateTime> range = Range.atLeast(new DateTime(2013, 1, 2, 3, 0, 0).withZoneRetainFields(DateTimeZone.UTC));
    final String ser = this.mapper.writeValueAsString(range);
    assertEquals(ser, "\"[2013-01-02T03:00:00+00:00 UTC,+∞)\"");
  }

  @Test
  public void testSerInfiniteRange() throws Exception
  {
    final Range<DateTime> range = Range.all();
    final String ser = this.mapper.writeValueAsString(range);
    assertEquals(ser, "\"(-∞,+∞)\"");
  }
}
