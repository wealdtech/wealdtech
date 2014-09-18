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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.jackson.JDoc;

import java.io.IOException;
import java.util.Map;

/**
 * Serializer for jdoc
 */
public class JDocSerializer extends StdSerializer<JDoc>
{
  protected JDocSerializer()
  {
    super(JDoc.class);
  }

  @Override
  public void serialize(final JDoc value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException
  {
    jgen.writeStartObject();
    for (final Map.Entry<String, Object> entry : value.getData().entrySet())
    {
      jgen.writeFieldName(entry.getKey());
      final Object val = entry.getValue();
      if (val instanceof JDoc)
      {
        serialize((JDoc)val, jgen, provider);
      }
      else
      {
        // Wrap it in quotes
        jgen.writeRawValue("\"" + entry.getValue() + "\"");
      }
    }
    jgen.writeEndObject();
  }
}