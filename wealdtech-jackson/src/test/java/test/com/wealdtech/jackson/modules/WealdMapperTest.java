/*
 * Copyright 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jackson.modules;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.jackson.WealdMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class WealdMapperTest
{

  // Ensure that longs are serialised correctly as plain values and not using scientific notation
  @Test
  public void testSerLong() throws Exception
  {
    final TestLongClass testClass = new TestLongClass(1000000000000L);

    final ObjectMapper mapper = WealdMapper.getMapper().copy();
    assertEquals(mapper.writeValueAsString(testClass), "{\"val\":1000000000000}");

    final ObjectMapper serverMapper = WealdMapper.getServerMapper().copy();
    assertEquals(serverMapper.writeValueAsString(testClass), "{\"val\":1000000000000}");

    final ObjectMapper simpleMapper = new ObjectMapper().copy().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    assertEquals(simpleMapper.writeValueAsString(testClass), "{\"val\":1000000000000}");
  }

  public static class TestLongClass
  {
    Long val;

    public TestLongClass(final Long val)
    {
      this.val = val;
    }
  }
}
