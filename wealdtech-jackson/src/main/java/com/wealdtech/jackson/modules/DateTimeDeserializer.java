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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeDeserializer.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER_ISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.sssZ");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ ZZZZ");
  private static final DateTimeFormatter DATE_TIME_FORMATTER_NO_TZ = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  @Override
  public DateTime deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    final String txt = jp.getText();
    if (txt == null)
    {
      return null;
    }

    return deserialize(txt);
  }

  public static DateTime deserialize(final String txt) throws IOException
  {
    DateTime result;
    if (txt.indexOf(' ') == -1)
    {
      // No timezone
      if (txt.indexOf('.') == -1)
      {
        // Use the Wealdtech format and set timezone to UTC
        result = DATE_TIME_FORMATTER_NO_TZ.parseDateTime(txt).withZone(DateTimeZone.UTC);
      }
      else
      {
        // ISO8601 format and set timezone to UTC
        result = DATE_TIME_FORMATTER_ISO.parseDateTime(txt).withZone(DateTimeZone.UTC);
      }
    }
    else
    {
      // Timezone supplied
      result = DATE_TIME_FORMATTER.parseDateTime(txt);
    }
    return result;
  }
}