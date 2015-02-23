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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Objects;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Custom serializer for Joda DateTime objects.
 * This serializer presents Joda DateTime objects as complex objects.
 * It provides a 'datetime' field which provides the DateTime in ISO 8601
 * format and also provides a 'timezone' field which provides the timezone
 * of the DateTime in such a format that the entire DateTime can be recreated
 * with no loss of information.
 */
public class DateTimeSerializer extends StdSerializer<DateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeSerializer.class);

  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ ZZZ");

  public DateTimeSerializer()
  {
    super(DateTime.class, true);
  }

  @Override
  public void serialize(final DateTime value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
    {
      gen.writeStartObject();
      gen.writeNumberField("timestamp", value.getMillis());
      if (!Objects.equal(value.getZone(), DateTimeZone.UTC))
      {
        gen.writeStringField("timezone", value.getZone().toString());
      }
      gen.writeEndObject();
    }
    else
    {
      gen.writeString(formatter.print(value));
    }
  }

  @Override
  public void serializeWithType(final DateTime value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, DateTime.class);
    if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
    {
      jgen.writeNumber(value.getMillis());
    }
    else
    {
      serialize(value, jgen, provider);
    }
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
