/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.jackson.JDoc;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.testng.Assert.*;

/**
 * Tests for JDoc
 */
public class JDocTest
{
  @Test
  public void testSer1()
  {
    try
    {
      final ImmutableMap<String, Object> subMap = ImmutableMap.<String, Object>of("sub1", Boolean.TRUE, "sub2", 5, "sub3", new InetSocketAddress("1.2.3.4", 80));
      final JDoc subDoc = new JDoc(subMap);

      final ImmutableMap<String, Object> map = ImmutableMap.<String, Object>of("val1", new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC), "val2", subDoc);
      final JDoc doc = new JDoc(map);
      final String docStr = WealdMapper.getServerMapper().writeValueAsString(doc);

      System.err.println(docStr);
    }
    catch (JsonProcessingException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeser1()
  {
    try
    {
      final String deser = "{\"val1\":\"2014-09-16T23:00:00Z\",\"val2\":{\"sub1\":\"true\",\"sub2\":\"5\",\"sub3\":\"/1.2.3.4:80\"}}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertEquals(doc.get("val1", new TypeReference<DateTime>() {}).orNull(),
                   new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC));
      final JDoc subDoc = doc.get("val2", new TypeReference<JDoc>(){}).orNull();
      assertNotNull(subDoc);
      assertEquals(subDoc.get("sub1", Boolean.class).orNull(), (Boolean)true);
      assertEquals(subDoc.get("sub2", new TypeReference<Integer>(){}).orNull(), (Integer)5);
      assertEquals(subDoc.get("sub3", InetSocketAddress.class).orNull(), new InetSocketAddress("/1.2.3.4", 80));
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  public static class JDoc2 extends JDoc
  {
    public JDoc2(final ImmutableMap<String, Object> data)
    {
      super(data);
    }
  }

  @Test
  public void testUpcast()
  {
    final ImmutableMap<String, Object> subMap = ImmutableMap.<String, Object>of("sub1", Boolean.TRUE, "sub2", 5, "sub3", new InetSocketAddress("1.2.3.4", 80));
    final JDoc subDoc = new JDoc(subMap);
    final ImmutableMap<String, Object> map = ImmutableMap.<String, Object>of("val1", new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC), "val2", subDoc);
    final JDoc doc = new JDoc(map);

    final JDoc2 doc2 = doc.get("val2", JDoc2.class).orNull();
    assertNotNull(doc2);
  }

}
