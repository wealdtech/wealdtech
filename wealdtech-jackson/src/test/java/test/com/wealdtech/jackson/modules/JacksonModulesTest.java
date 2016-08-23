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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.utils.WealdInterval;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Locale;

import static org.testng.Assert.*;

public class JacksonModulesTest
{
  private final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper().copy();

  @BeforeClass
  public void setUp()
  {
    this.mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testDeserAbsent() throws Exception
  {
    final Optional<?> value = this.mapper.readValue("null", new TypeReference<Optional<String>>()
    {
    });
    assertFalse(value.isPresent());
  }

  @Test
  public void testDeserSimpleString() throws Exception
  {
    final Optional<?> value = this.mapper.readValue("\"simpleString\"", new TypeReference<Optional<String>>()
    {
    });
    assertTrue(value.isPresent());
    assertEquals("simpleString", value.get());
  }

  @Test
  public void testDeserInsideObject() throws Exception
  {
    final OptionalData data = this.mapper.readValue("{\"mystring\":\"simpleString\"}", OptionalData.class);
    assertTrue(data.myString.isPresent());
    assertEquals("simpleString", data.myString.get());
  }

  @Test
  public void testDeserComplexObject() throws Exception
  {
    final TypeReference<Optional<OptionalData>> type = new TypeReference<Optional<OptionalData>>()
    {
    };
    final Optional<OptionalData> data = this.mapper.readValue("{\"mystring\":\"simpleString\"}", type);
    assertTrue(data.isPresent());
    assertTrue(data.get().myString.isPresent());
    assertEquals("simpleString", data.get().myString.get());
  }

  @Test
  public void testDeserNull() throws Exception
  {
    final TypeReference<Optional<OptionalData>> type = new TypeReference<Optional<OptionalData>>()
    {
    };
    final Optional<OptionalData> data = this.mapper.readValue("{}", type);
    assertTrue(data.isPresent());
  }

  @Test
  public void testDeserGeneric() throws Exception
  {
    final TypeReference<Optional<OptionalGenericData<String>>> type = new TypeReference<Optional<OptionalGenericData<String>>>()
    {
    };
    final Optional<OptionalGenericData<String>> data = this.mapper.readValue("{\"mydata\":\"simpleString\"}", type);
    assertTrue(data.isPresent());
    assertTrue(data.get().myData.isPresent());
    assertEquals("simpleString", data.get().myData.get());
  }

  @Test
  public void testDeserDateTime() throws Exception
  {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ ZZZ");

    // Complete serialization
    final DateTime dt1 = this.mapper.readValue("\"2012-02-03T04:05:06+0100 Europe/Paris\"", DateTime.class);
    final DateTime basedt1 = DateTime.parse("2012-02-03 04:05:06+0100 Europe/Paris", fmt);
    assertEquals(dt1, basedt1);

    // Serialization without timezone (== UTC)
    final DateTime dt2 = this.mapper.readValue("\"2012-02-03T04:05:06+0100\"", DateTime.class);
    final DateTime basedt2 = DateTime.parse("2012-02-03 04:05:06+0100 UTC", fmt);
    assertEquals(dt2, basedt2);

    // Serialization with UTC timezone
    final DateTime dt3 = this.mapper.readValue("\"2012-02-03T04:05:06+0100 UTC\"}", DateTime.class);
    final DateTime basedt3 = DateTime.parse("2012-02-03 04:05:06+0100 UTC", fmt);
    assertEquals(dt3, basedt3);

    // Serialization with differing timezone to offset
    final DateTime dt4 = this.mapper.readValue("\"2012-02-03T04:05:06+0800 Europe/Paris\"}", DateTime.class);
    final DateTime basedt4 = DateTime.parse("2012-02-03 04:05:06+0800 Europe/Paris", fmt);
    assertEquals(dt4, basedt4);

    // Serialization with : in the timezone offset
    final DateTime dt5 = this.mapper.readValue("\"2013-11-04T16:00:00+01:00 Europe/Paris\"", DateTime.class);
    final DateTime basedt5 = DateTime.parse("2013-11-04 16:00:00+01:00 Europe/Paris", fmt);
    assertEquals(dt5, basedt5);

    // serialization in parts
    final DateTime dt6 = this.mapper.readValue("{\"timestamp\":1375916523000,\"timezone\":\"Europe/Paris\"}", DateTime.class);
    final DateTime basedt6 = DateTime.parse("2013-08-08 01:02:03+02:00 Europe/Paris", fmt);
    assertEquals(dt6, basedt6);
  }

  @Test
  public void testDeserLocalDateTime() throws Exception
  {
    // Complete serialization
    final LocalDateTime ldt1 = this.mapper.readValue("\"2012-02-03T04:05:06\"", LocalDateTime.class);
    final LocalDateTime baseldt1 = LocalDateTime.parse("2012-02-03T04:05:06");
    assertEquals(ldt1, baseldt1);
  }

  @Test
  public void testDeserLocalDateTime2() throws Exception
  {
    // Complete serialization
    final LocalDateTime ldt1 = this.mapper.readValue("1328241906000", LocalDateTime.class);
    final LocalDateTime baseldt1 = LocalDateTime.parse("2012-02-03T04:05:06");
    assertEquals(ldt1, baseldt1);
  }

  @Test
  public void testDeserLocalDate() throws IOException
  {
    final LocalDate ld1 = this.mapper.readValue("\"2012-02-03\"", LocalDate.class);
    final LocalDate baseld1 = LocalDate.parse("2012-02-03");
    assertEquals(ld1, baseld1);
  }

  @Test
  public void testDeserPeriod() throws IOException
  {
    final Period p1 = this.mapper.readValue("\"P1W2DT5M\"", Period.class);
    final Period basep1 = Period.parse("P1W2DT5M");
    assertEquals(p1, basep1);
  }

  @Test

  public void testDeserOffsetDateTime() throws IOException
  {
    final DateTime dt1 = this.mapper.readValue("\"2014-06-02T00:00:00.000+01:00\"", DateTime.class);
    assertEquals(dt1, new DateTime(2014, 6, 2, 0, 0, 0, DateTimeZone.forID("Europe/London")).withZone(DateTimeZone.UTC));
  }
  @Test
  public void testDeserInvalidDateTime() throws Exception
  {
    // Invalid timezone
    try
    {
      this.mapper.readValue("\"20120203T040506+0100 Neverwhere\"", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IllegalArgumentException iae) // NOPMD
    {
      // Good
    }

    // No datetime
    try
    {
      this.mapper.readValue("\" Europe/Paris\"", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IllegalArgumentException iae) // NOPMD
    {
      // Good
    }

    // Invalid datettime
    try
    {
      this.mapper.readValue("\"20121503T040506+0100 Europe/Paris\"", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IllegalArgumentException iae) // NOPMD
    {
      // Good
    }
  }

  @Test
  public void testDeserInvalidLocalDateTime() throws Exception
  {
    // Invalid offset
    try
    {
      this.mapper.readValue("\"20120203T040506+0100\"", LocalDateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // Invalid format
    try
    {
      this.mapper.readValue("\"20120203 040506\"", LocalDateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // Invalid value
    try
    {
      this.mapper.readValue("\"20121403T040506\"", LocalDateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }
  }

  @Test
  public void testDeserInvalidPeriod() throws Exception
  {
    // Invalid period
    try
    {
      this.mapper.readValue("\"P50X\"", Period.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }
  }

  @Test
  public void testDeserInvalidLocalDate() throws Exception
  {
    // Invalid value
    try
    {
      this.mapper.readValue("\"20121403\"", LocalDate.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }
  }

  @Test
  public void testDeserDateTimeZone() throws Exception
  {
    // Complete serialization
    final DateTimeZone dtz = this.mapper.readValue("\"America/New_York\"", DateTimeZone.class);
    assertEquals(dtz, DateTimeZone.forID("America/New_York"));
  }

  @Test
  public void testDeserDateTimeZoneInvalid() throws Exception
  {
    try
    {
      this.mapper.readValue("\"Bad/Bad\"", DateTimeZone.class);
      fail("Managed to deserialize invalid timezone");
    }
    catch (IOException ioe)
    {
      // Good
    }
  }

  @Test
  public void testDeserWealdInterval() throws Exception
  {
    final WealdInterval int1 = this.mapper.readValue("{\"start\":\"2013-08-08T01:02:03+0200 Europe/Paris\",\"end\":\"2013-08-08T01:02:05+0100 Europe/London\"}", WealdInterval.class);

    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final WealdInterval int2 = new WealdInterval(fromDt, toDt);
    assertEquals(int1, int2);
  }

  @Test
  public void testSerAbsent() throws Exception
  {
    final String value = this.mapper.writeValueAsString(Optional.absent());
    assertEquals("null", value);
  }

  @Test
  public void testSerSimpleString() throws Exception
  {
    final String value = this.mapper.writeValueAsString(Optional.of("simpleString"));
    assertEquals("\"simpleString\"", value);
  }

  @Test
  public void testSerInsideObject() throws Exception
  {
    final OptionalData data = new OptionalData();
    data.myString = Optional.of("simpleString");
    final String value = this.mapper.writeValueAsString(data);
    assertEquals("{\"mystring\":\"simpleString\"}", value);
  }

  @Test
  public void testSerComplexObject() throws Exception
  {
    final OptionalData data = new OptionalData();
    data.myString = Optional.of("simpleString");
    final String value = this.mapper.writeValueAsString(Optional.of(data));
    assertEquals("{\"mystring\":\"simpleString\"}", value);
  }

  @Test
  public void testSerGeneric() throws Exception
  {
    final OptionalGenericData<String> data = new OptionalGenericData<String>();
    data.myData = Optional.of("simpleString");
    final String value = this.mapper.writeValueAsString(Optional.of(data));
    assertEquals("{\"mydata\":\"simpleString\"}", value);
  }

  @Test
  public void testSerNonNull() throws Exception
  {
    final OptionalData data = new OptionalData();
    data.myString = Optional.absent();
    final String value = this.mapper.writeValueAsString(data);
    assertEquals("{}", value);
  }

  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  public static final class OptionalData
  {
    private Optional<String> myString;
  }

  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  public static final class OptionalGenericData<T>
  {
    private Optional<T> myData;
  }

  @Test
  public void testSerDateTimeAsLong() throws Exception
  {
    final DateTime dt1 = DateTime.parse("2012-02-03T04:05:06+0100").withZone(DateTimeZone.forID("Europe/London"));
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writeValueAsString(dt1);
    assertEquals(value, "{\"timestamp\":1328238306000,\"timezone\":\"Europe/London\"}");
  }

  @Test
  public void testSerUTCDateTimeAsLong() throws Exception
  {
    final DateTime dt1 = DateTime.parse("2012-02-03T04:05:06+0100").withZone(DateTimeZone.UTC);
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writeValueAsString(dt1);
    assertEquals(value, "{\"timestamp\":1328238306000}");
  }

  @Test
  public void testSerDateTimeAsString() throws Exception
  {
    final DateTime dt1 = DateTime.parse("2012-02-03T04:05:06+0100").withZone(DateTimeZone.forID("Europe/London"));
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(dt1);
    assertEquals(value, "\"2012-02-03T03:05:06.000+00:00 Europe/London\"");
  }

  @Test
  public void testSerLocalDateTimeAsLong() throws Exception
  {
    final LocalDateTime ldt1 = LocalDateTime.parse("2012-02-03T04:05:06");
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writeValueAsString(ldt1);
    assertEquals(value, "1328241906000");
  }

  @Test
  public void testSerLocalDateTimeAsString() throws Exception
  {
    final LocalDateTime ldt1 = LocalDateTime.parse("2012-02-05T04:05:06");
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(ldt1);
    // LocalDateTime is stored in UTC so need to translate it to a datetime in the local zone then to UTC prior to the string representation
    assertEquals(value, ldt1.toDateTime(DateTimeZone.getDefault()).withZone(DateTimeZone.UTC).toString("\"yyyy-MM-dd'T'HH:mm:ss\""));
  }

  @Test
  public void testSerLocalDateTime2AsString() throws Exception
  {
    final DateTime dt1 = new DateTime(2012, 5, 6, 10, 2, 3, DateTimeZone.getDefault());
    final LocalDateTime ldt1 = dt1.toLocalDateTime();
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(ldt1);
    // LocalDateTime is stored in UTC so need to translate it to UTC prior to the string representation
    assertEquals(value, dt1.withZone(DateTimeZone.UTC).toString("\"yyyy-MM-dd'T'HH:mm:ss\""));
  }

  @Test
  public void testSerLocalDateTime2AsLong() throws Exception
  {
    final DateTime dt1 = new DateTime(2012, 5, 6, 10, 2, 3, DateTimeZone.forID("Europe/London"));
    final LocalDateTime ldt1 = dt1.toLocalDateTime();
    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writeValueAsString(ldt1);
    assertEquals(value, "1336298523000");
  }

  @Test
  public void testSerLocalDate() throws Exception
  {
    final LocalDate lt1 = LocalDate.parse("2012-02-03");
    final String value = this.mapper.writeValueAsString(lt1);
    assertEquals(value, "\"2012-02-03\"");
  }

  @Test
  public void testSerPeriod() throws Exception
  {
    final Period p1 = Period.parse("PT2H20M");
    final String value = this.mapper.writeValueAsString(p1);
    assertEquals(value, "\"PT2H20M\"");
  }

  @Test
  public void testSerDateTimeZone() throws Exception
  {
    final DateTimeZone dtz = DateTimeZone.forID("America/New_York");
    final String value = this.mapper.writeValueAsString(dtz);
    assertEquals(value, "\"America/New_York\"");
  }

  @Test
  public void testSerWealdInterval() throws Exception
  {
    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final WealdInterval interval = new WealdInterval(fromDt, toDt);

    final String value = this.mapper.writeValueAsString(interval);
    assertEquals(value, "{\"start\":{\"timestamp\":1375916523000,\"timezone\":\"Europe/Paris\"},\"end\":{\"timestamp\":1375920125000,\"timezone\":\"Europe/London\"}}");
  }

  @Test
  public void testSerDateTimeRangeAsString() throws Exception
  {
    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final Range<DateTime> range = Range.closedOpen(fromDt, toDt);

    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(range);
    assertEquals(value, "\"[2013-08-08T01:02:03+02:00 Europe/Paris,2013-08-08T01:02:05+01:00 Europe/London)\"");
  }

  @Test
  public void testDeserDateTimeRangeAsString() throws Exception
  {
    final String value = "\"[2013-08-08T01:02:03+02:00 Europe/Paris,2013-08-08T01:02:05+01:00 Europe/London)\"";
    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final Range<DateTime> range = Range.closedOpen(fromDt, toDt);

    final Range<DateTime> deser = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).readValue(value, new TypeReference<Range<DateTime>>(){});
    assertEquals(range, deser);
  }

  @Test
  public void testSerDateTimeRangeAsTimestamps() throws Exception
  {
    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final Range<DateTime> range = Range.closedOpen(fromDt, toDt);

    final String value = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writeValueAsString(range);
    assertEquals(value, "{\"from\":{\"timestamp\":1375916523000,\"timezone\":\"Europe/Paris\"},\"to\":{\"timestamp\":1375920125000,\"timezone\":\"Europe/London\"}}");
  }

  @Test
  public void testDeserDateTimeRangeAsTimestamps() throws Exception
  {
    final String value = "{\"from\":{\"timestamp\":1375916523000,\"timezone\":\"Europe/Paris\"},\"to\":{\"timestamp\":1375920125000,\"timezone\":\"Europe/London\"}}";
    final DateTimeZone fromDtz = DateTimeZone.forID("Europe/Paris");
    final DateTime fromDt = DateTime.parse("2013-08-08T01:02:03+0200").withZone(fromDtz);
    final DateTimeZone toDtz = DateTimeZone.forID("Europe/London");
    final DateTime toDt = DateTime.parse("2013-08-08T01:02:05+0100").withZone(toDtz);
    final Range<DateTime> range = Range.closedOpen(fromDt, toDt);

    final Range<DateTime> deser = this.mapper.copy().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).readValue(value, new TypeReference<Range<DateTime>>(){});
    assertEquals(range, deser);
  }

  @Test
  public void testSerYearMonth() throws Exception
  {
    final YearMonth ym1 = new YearMonth(19, 9);
    final String value = this.mapper.writeValueAsString(ym1);
    assertEquals(value, "\"09/19\"");
  }

  @Test
  public void testDeserYearMonth() throws Exception
  {
    final String value = "\"01/01\"";
    final YearMonth deser = this.mapper.readValue(value, YearMonth.class);
    assertEquals(deser, new YearMonth(2001, 1));
  }

  @Test
  public void testSerLocale1() throws Exception
  {
    final Locale locale = Locale.UK;
    final String value = this.mapper.writeValueAsString(locale);
    assertEquals(value, "\"en-GB\"");
  }

  @Test
  public void testDeserLocale1() throws Exception
  {
    final String value = "\"en\"";
    final Locale locale = this.mapper.readValue(value, Locale.class);
    assertEquals(locale.getLanguage(), "en");
  }

  @Test
  public void testDeserLocale2() throws Exception
  {
    final String value = "\"en-GB\"";
    final Locale locale = this.mapper.readValue(value, Locale.class);
    assertEquals(locale.getLanguage(), "en");
    assertEquals(locale.getCountry(), "GB");
  }
}

