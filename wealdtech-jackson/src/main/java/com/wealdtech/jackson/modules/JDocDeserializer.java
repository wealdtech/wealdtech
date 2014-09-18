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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.jackson.JDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO Does not handle arrays of anything other than values or jdocs; need to restructure to recurse properly
 */
public class JDocDeserializer extends StdDeserializer<JDoc>
{
  private static final Logger LOG = LoggerFactory.getLogger(JDoc.class);

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

    // Objects are always treated as JDocs
    while (jp.nextToken() != JsonToken.END_OBJECT)
    {
      final String key = jp.getCurrentName();
      LOG.trace("Key is {}", key);
      jp.nextToken();

      final Object value;
      if (jp.getCurrentToken() == JsonToken.START_OBJECT)
      {
        // Child jdoc
        value = deserialize(jp, ctxt);
        LOG.trace("Value is object {}", value);
      }
      else if (jp.getCurrentToken() == JsonToken.START_ARRAY)
      {
        final ImmutableList.Builder<Object> array = ImmutableList.builder();
        LOG.trace("Value is array");
        while (jp.nextToken() != JsonToken.END_ARRAY)
        {
          final Object arrayValue;
          if (jp.getCurrentToken() == JsonToken.START_OBJECT)
          {
            // Child jdoc; store it as a raw string
            arrayValue = deserialize(jp, ctxt);
            LOG.trace("Value is object: {}", arrayValue);
            array.add(arrayValue);
          }
          else
          {
            arrayValue = jp.getText();
            LOG.trace("Value is simple {}", arrayValue);
            array.add(arrayValue);
          }
        }
        value = array.build();
      }
      else
      {
        // Straight value
        value = jp.getText();
        LOG.trace("Value is simple {}", value);
      }
      dataB.put(key, value);
    }
    return new JDoc(dataB.build());
  }
}
