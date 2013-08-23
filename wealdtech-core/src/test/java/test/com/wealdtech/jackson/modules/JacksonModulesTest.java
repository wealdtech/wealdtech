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

package test.com.wealdtech.jackson.modules;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.utils.WealdInterval;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

public class JacksonModulesTest
{
  private final transient ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();

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
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZZ");

    // Complete serialization
    final DateTime dt1 = this.mapper.readValue("{\"datetime\":\"2012-02-03T04:05:06+0100\",\"timezone\":\"Europe/Paris\"}", DateTime.class);
    final DateTime basedt1 = DateTime.parse("2012-02-03 04:05:06+0100", fmt).withZone(DateTimeZone.forID("Europe/Paris"));
    assertEquals(dt1, basedt1);

    // Serialization without timezone (== UTC)
    final DateTime dt2 = this.mapper.readValue("{\"datetime\":\"2012-02-03T04:05:06+0100\"}", DateTime.class);
    final DateTime basedt2 = DateTime.parse("2012-02-03 04:05:06+0100", fmt).withZone(DateTimeZone.forID("UTC"));
    assertEquals(dt2, basedt2);

    // Serialization with UTC timezone
    final DateTime dt3 = this.mapper.readValue("{\"datetime\":\"2012-02-03T04:05:06+0100\",\"timezone\":\"UTC\"}", DateTime.class);
    final DateTime basedt3 = DateTime.parse("2012-02-03 04:05:06+0100", fmt).withZone(DateTimeZone.forID("UTC"));
    assertEquals(dt3, basedt3);

    // Serialization with differing timezones
    final DateTime dt4 = this.mapper.readValue("{\"datetime\":\"2012-02-03T04:05:06+0800\",\"timezone\":\"Europe/Paris\"}", DateTime.class);
    final DateTime basedt4 = DateTime.parse("2012-02-03 04:05:06+0800", fmt).withZone(DateTimeZone.forID("Europe/Paris"));
    assertEquals(dt4, basedt4);

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
  public void testDeserLocalDate() throws Exception
  {
    final LocalDate ld1 = this.mapper.readValue("\"2012-02-03\"", LocalDate.class);
    final LocalDate baseld1 = LocalDate.parse("2012-02-03");
    assertEquals(ld1, baseld1);
  }

  @Test
  public void testDeserPeriod() throws Exception
  {
    final Period p1 = this.mapper.readValue("\"P1W2DT5M\"", Period.class);
    final Period basep1 = Period.parse("P1W2DT5M");
    assertEquals(p1, basep1);
  }

  @Test
  public void testDeserInvalidDateTime() throws Exception
  {
    // Invalid timezone
    try
    {
      this.mapper.readValue("{\"datetime\":\"20120203T040506+0100\",\"timezone\":\"Neverwhere\"}", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // No datetime
    try
    {
      this.mapper.readValue("{\"timezone\":\"Europe/Paris\"}", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
    {
      // Good
    }

    // Invalid datettime
    try
    {
      this.mapper.readValue("{\"datetime\":\"20121503T040506+0100\",\"timezone\":\"Europe/Paris\"}", DateTime.class);
      // Should not reach here
      fail();
    }
    catch (IOException ioe) // NOPMD
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
    final WealdInterval int1 = this.mapper.readValue("{\"start\":{\"datetime\":\"2013-08-08T01:02:03+0200\",\"timezone\":\"Europe/Paris\"},\"end\":{\"datetime\":\"2013-08-08T01:02:05+0100\",\"timezone\":\"Europe/London\"}}", WealdInterval.class);

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
  public void testSerDateTime() throws Exception
  {
    final DateTime dt1 = DateTime.parse("2012-02-03T04:05:06+0100");
    final String value = this.mapper.writeValueAsString(dt1);
    assertEquals(value, "{\"datetime\":\"2012-02-03T04:05:06+0100\",\"timezone\":\"+01:00\"}");
  }

  @Test
  public void testSerLocalDateTime() throws Exception
  {
    final LocalDateTime ldt1 = LocalDateTime.parse("2012-02-03T04:05:06");
    final String value = this.mapper.writeValueAsString(ldt1);
    assertEquals(value, "\"2012-02-03T04:05:06\"");
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
    assertEquals(value, "{\"start\":{\"datetime\":\"2013-08-08T01:02:03+0200\",\"timezone\":\"Europe/Paris\"},\"end\":{\"datetime\":\"2013-08-08T01:02:05+0100\",\"timezone\":\"Europe/London\"}}");
  }
}
