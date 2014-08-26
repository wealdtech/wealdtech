/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.TriVal;

import java.io.IOException;


public class TriValSerializer extends StdSerializer<TriVal<?>>
{
  public TriValSerializer(JavaType type)
  {
    super(type);
  }

  @Override
  public void serialize(TriVal<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException
  {
    if (value.isAbsent())
    {
      provider.defaultSerializeNull(jgen);
    }
    else if (value.isClear())
    {
      provider.defaultSerializeValue("", jgen);
    }
    else
    {
      provider.defaultSerializeValue(value.get(), jgen);
    }
  }

  @Override
  public void serializeWithType(final TriVal<?> value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, TriVal.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
