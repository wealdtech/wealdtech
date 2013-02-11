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

import java.net.InetSocketAddress;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wealdtech.jackson.ObjectMapperFactory;

import static org.testng.Assert.*;

public class MiscModuleTest
{
  private final transient ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();

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
  public void testSerInetSocketAddress() throws Exception
  {
    final InetSocketAddress addr = new InetSocketAddress("www.wealdtech.com", 23456);
    final String ser = this.mapper.writeValueAsString(addr);
    assertEquals(ser, "\"www.wealdtech.com:23456\"");
  }
}
