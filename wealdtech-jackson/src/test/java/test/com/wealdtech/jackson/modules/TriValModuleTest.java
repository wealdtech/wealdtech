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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wealdtech.TriVal;
import com.wealdtech.jackson.ObjectMapperFactory;

import static org.testng.Assert.*;

public class TriValModuleTest
{
  private final transient ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper().copy();

  @BeforeClass
  public void setUp()
  {
    this.mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testDeserStrPresent() throws Exception
  {
    final String ser = "\"test\"";
    final TriVal<String> deser = this.mapper.readValue(ser, new TypeReference<TriVal<String>>(){});
    assertTrue(deser.isPresent());
    assertEquals(deser.get(), "test");
  }

  @Test
  public void testDeserStrClear() throws Exception
  {
    final String ser = "\"\"";
    final TriVal<String> deser = this.mapper.readValue(ser, new TypeReference<TriVal<String>>(){});
    assertTrue(deser.isClear());
  }

  @Test
  public void testSerStrPresent() throws Exception
  {
    final TriVal<String> trv = TriVal.<String>of("a test");
    final String ser = this.mapper.writeValueAsString(trv);
    assertEquals(ser, "\"a test\"");
  }

  @Test
  public void testSerStrClear() throws Exception
  {
    final TriVal<String> trv = TriVal.clear();
    final String ser = this.mapper.writeValueAsString(trv);
    assertEquals(ser, "\"\"");
  }


  @Test
  public void testDeserDateTimePresent() throws Exception
  {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZZ");

    final String ser = "\"2012-02-03T04:05:06+0100 Europe/Paris\"";
    final TriVal<DateTime> deser = this.mapper.readValue(ser, new TypeReference<TriVal<DateTime>>(){});

    final TriVal<DateTime> val = TriVal.<DateTime>of(DateTime.parse("2012-02-03 04:05:06+0100", fmt).withZone(DateTimeZone.forID("Europe/Paris")));
    assertEquals(deser, val);
  }

  @Test
  public void testDeserDateTimeClear() throws Exception
  {
    final String ser = "\"\"";
    final TriVal<DateTime> deser = this.mapper.readValue(ser, new TypeReference<TriVal<String>>(){});
    assertTrue(deser.isClear());
  }

  @Test
  public void testSerDateTimePresent() throws Exception
  {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZZ");

    final TriVal<DateTime> trv = TriVal.<DateTime>of(DateTime.parse("2012-02-03 04:05:06+0100", fmt).withZone(DateTimeZone.forID("Europe/Paris")));
    final String ser = this.mapper.writeValueAsString(trv);
    assertEquals(ser, "\"2012-02-03T04:05:06+01:00 Europe/Paris\"");
  }

  @Test
  public void testSerDateTimeClear() throws Exception
  {
    final TriVal<DateTime> trv = TriVal.clear();
    final String ser = this.mapper.writeValueAsString(trv);
    assertEquals(ser, "\"\"");
  }
}
