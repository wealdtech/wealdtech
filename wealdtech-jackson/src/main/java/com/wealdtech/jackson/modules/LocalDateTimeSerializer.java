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
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Custom serializer for Joda LocalDateTime objects.
 * This serializer presents Joda LocalDateTime objects in ISO 8601 format with an implicit time zone of UTC.
 */
public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime>
{
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);

  public LocalDateTimeSerializer()
  {
    super(LocalDateTime.class, true);
  }

  @Override
  public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
    {
      gen.writeNumber(value.toDateTime(DateTimeZone.UTC).getMillis());
    }
    else
    {
      gen.writeString(formatter.print(value.toDateTime().withZone(DateTimeZone.UTC)));
    }
  }

  @Override
  public void serializeWithType(final LocalDateTime value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, LocalDateTime.class);
    if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
    {
      jgen.writeNumber(value.toDateTime(DateTimeZone.UTC).getMillis());
    }
    else
    {
      serialize(value, jgen, provider);
    }
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
