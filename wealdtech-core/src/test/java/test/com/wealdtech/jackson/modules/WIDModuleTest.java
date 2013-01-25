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

import java.util.Date;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import com.wealdtech.WID;
import com.wealdtech.jackson.ObjectMapperFactory;

import static org.testng.Assert.*;

public class WIDModuleTest
{
  private final transient ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();

  @BeforeClass
  public void setUp()
  {
    this.mapper.disable(SerializationFeature.INDENT_OUTPUT);
  }

  @Test
  public void testDeserWID() throws Exception
  {
    final String ser = "\"20a2a19b20000a\"";
    final WID<Date> deser = this.mapper.readValue(ser, new TypeReference<WID<Date>>(){});
    assertEquals(deser, WID.<Date>fromComponents(5, 1500000000000L, 10));
  }

  @Test
  public void testDeserWIDMap() throws Exception
  {
    final String ser = "{\"20a2a19b20000a\":\"Test\"}";
    final Map<WID<Date>, String> deser = this.mapper.readValue(ser, new TypeReference<Map<WID<Date>, String>>(){});
    assertNotNull(deser);
    assertEquals(deser.size(), 1);
    assertNotNull(deser.get(WID.<Date>fromComponents(5, 1500000000000L, 10)));
  }

  @Test
  public void testSerWID() throws Exception
  {
    final WID<Date> wid = WID.fromComponents(5, 1500000000000L, 10);
    final String ser = this.mapper.writeValueAsString(wid);
    assertEquals(ser, "\"20a2a19b20000a\"");
  }

  @Test
  public void testSerWIDMap() throws Exception
  {
    final Map<WID<Date>, String> widMap = Maps.newHashMap();
    widMap.put(WID.<Date>fromComponents(5, 1500000000000L, 10), "Test");
    final String ser = this.mapper.writeValueAsString(widMap);
    assertEquals(ser, "{\"20a2a19b20000a\":\"Test\"}");
  }
}
