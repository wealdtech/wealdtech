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

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.jackson.RawJsonDeserializer;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Test serialization/deserialization of raw JSON
 */
public class RawJsonTest
{
  public static class OuterClass
  {
    public int outer;
    @JsonDeserialize(using=RawJsonDeserializer.class)
    @JsonRawValue
    public String inner;
  }

  public static class InnerClass
  {
    public int inner;
  }


  @Test
  public void testDeser() throws IOException
  {
    final String deser = "{\"outer\":3,\"inner\":{\"inner\":4}}";

    final OuterClass outer = new ObjectMapper().readValue(deser, OuterClass.class);
    assertEquals(outer.outer, 3);
    assertEquals(outer.inner, "{\"inner\":4}");

    final InnerClass inner = new ObjectMapper().readValue(outer.inner, InnerClass.class);
    assertEquals(inner.inner, 4);
  }

  @Test
  public void testSer() throws IOException
  {
    final InnerClass inner = new InnerClass();
    inner.inner = 5;
    final OuterClass outer = new OuterClass();
    outer.outer = 6;
    outer.inner = new ObjectMapper().writeValueAsString(inner);
    assertEquals(outer.inner, "{\"inner\":5}");

    final String outerStr = new ObjectMapper().writeValueAsString(outer);
    assertEquals(outerStr, "{\"outer\":6,\"inner\":{\"inner\":5}}");
  }

  public static class MapClass
  {
    @JsonSerialize(using=MapClassSerializer.class)
    public ImmutableMap<String, String> data;
//    public final MapClassData data;

    public MapClass(final ImmutableMap<String, String> data)
    {
      this.data = data;
    }

//    @JsonCreator
//    private MapClass(final MapClassData data)
//    {
//      this.data = data;
//    }
  }

  public static class MapClassSerializer extends JsonSerializer<ImmutableMap<String, String>>
  {
    @Override
    public void serialize(final ImmutableMap<String, String> value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException, JsonProcessingException
    {
      jgen.writeStartObject();
      boolean first = true;
      for (final Map.Entry<String, String> entry : value.entrySet())
      {
        if (!first)
        {
          jgen.writeRaw(",");
        }
        else
        {
          first = false;
        }
        jgen.writeFieldName(entry.getKey());
        jgen.writeRaw(":");
        jgen.writeRaw(entry.getValue());
      }
      jgen.writeEndObject();
    }
  }

  @Test
  public void testMapSer() throws IOException
  {
    final ImmutableMap<String, String> subMap1 = ImmutableMap.of("sub1key1", "sub1val1", "sub1key2", "sub1val2");
    final String subMap1Str = new ObjectMapper().writeValueAsString(subMap1);
    assertEquals(subMap1Str, "{\"sub1key1\":\"sub1val1\",\"sub1key2\":\"sub1val2\"}");

    final ImmutableMap<String, String> subMap2 = ImmutableMap.of("sub2key1", "sub2val1", "sub2key2", "sub2val2");
    final String subMap2Str = new ObjectMapper().writeValueAsString(subMap2);
    assertEquals(subMap2Str, "{\"sub2key1\":\"sub2val1\",\"sub2key2\":\"sub2val2\"}");

    final MapClass mapClass = new MapClass(ImmutableMap.of("submap1", subMap1Str, "submap2", subMap2Str));
    final String mapStr = new ObjectMapper().writeValueAsString(mapClass);
    assertEquals(mapStr, "{\"data\":{\"submap1\":{\"sub1key1\":\"sub1val1\",\"sub1key2\":\"sub1val2\"},\"submap2\":{\"sub2key1\":\"sub2val1\",\"sub2key2\":\"sub2val2\"}}}");
  }

  @Test
  public void testMapDeser() throws IOException
  {
    final ImmutableMap<String, String> subMap1 = ImmutableMap.of("sub1key1", "sub1val1", "sub1key2", "sub1val2");
    final String subMap1Str = new ObjectMapper().writeValueAsString(subMap1);
    assertEquals(subMap1Str, "{\"sub1key1\":\"sub1val1\",\"sub1key2\":\"sub1val2\"}");

    final ImmutableMap<String, String> subMap2 = ImmutableMap.of("sub2key1", "sub2val1", "sub2key2", "sub2val2");
    final String subMap2Str = new ObjectMapper().writeValueAsString(subMap2);
    assertEquals(subMap2Str, "{\"sub2key1\":\"sub2val1\",\"sub2key2\":\"sub2val2\"}");

    final MapClass mapClass = new MapClass(ImmutableMap.of("submap1", subMap1Str, "submap2", subMap2Str));

    final MapClass deserMapClass = new ObjectMapper().readValue("{\"data\":{\"submap1\":{\"sub1key1\":\"sub1val1\",\"sub1key2\":\"sub1val2\"},\"submap2\":{\"sub2key1\":\"sub2val1\",\"sub2key2\":\"sub2val2\"}}}",
                                                                MapClass.class);

    assertEquals(deserMapClass, mapClass);
  }
}
