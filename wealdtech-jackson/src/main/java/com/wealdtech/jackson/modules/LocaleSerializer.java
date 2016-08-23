/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * Custom serializer for Locale objects to represent them using a dash rather than underscore as the separator
 */
public class LocaleSerializer extends StdSerializer<Locale>
{
  private static final char STANDARD_SPLITTER_CHAR = '-';

  private static final Logger LOGGER = LoggerFactory.getLogger(LocaleSerializer.class);

  public LocaleSerializer()
  {
    super(Locale.class, true);
  }

  @Override
  public void serialize(final Locale value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    if (value != null)
    {
      gen.writeString(value.toString().replaceAll("_", "-"));
    }
  }

  @Override
  public void serializeWithType(final Locale value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, DateTime.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
