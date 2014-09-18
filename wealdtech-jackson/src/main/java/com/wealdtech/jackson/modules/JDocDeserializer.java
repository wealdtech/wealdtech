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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.jackson.JDoc;

import java.io.IOException;

/**
 */
public class JDocDeserializer extends StdDeserializer<JDoc>
{
  private static final long serialVersionUID = 6030347476748849469L;

  protected JDocDeserializer()
  {
    super(JDoc.class);
  }

  @Override
  public JDoc deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
  {
    if (jp.getCurrentToken() != JsonToken.START_OBJECT)
    {
      throw new IOException("invalid start marker " + jp.getText() + " " + jp.getValueAsString());
    }

    final ImmutableMap.Builder<String, Object> dataB = ImmutableMap.builder();

    // Everything is a string, unless it's an object in which case it's another jdoc
    while (jp.nextToken() != JsonToken.END_OBJECT)
    {
      final String key = jp.getCurrentName();
      jp.nextToken();

      final Object value;
      if (jp.getCurrentToken() == JsonToken.START_OBJECT)
      {
        // Child jdoc; store it as a raw string
        value = deserialize(jp, ctxt);
      }
      else
      {
        // Straight value
        value = jp.getText();
      }
      dataB.put(key, value);
    }
    return new JDoc(dataB.build());
  }
}
