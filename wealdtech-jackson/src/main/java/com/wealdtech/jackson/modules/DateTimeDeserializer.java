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
    final JsonToken token = jp.getCurrentToken();
    if (token == JsonToken.VALUE_NUMBER_INT)
    {
      return new DateTime(jp.getLongValue(), DateTimeZone.UTC);
    }
    else if (token == JsonToken.START_OBJECT)
    {
      String timezone = null;
      Long timestamp = null;
      while (jp.nextToken() != JsonToken.END_OBJECT)
      {
        final String fieldName = jp.getCurrentName();
        jp.nextToken();
        if ("timestamp".equals(fieldName))
        {
          timestamp = jp.getLongValue();
        }
        else if ("timezone".equals(fieldName))
        {
          timezone = jp.getText();
        }
      }
      if (timezone == null)
      {
        return new DateTime(timestamp).withZone(DateTimeZone.UTC);
      }
      else
      {
        return new DateTime(timestamp).withZone(DateTimeZone.forID(timezone));
      }
    }
    final String txt = jp.getText();
    if (txt == null)
    {
      return null;
    }

    return deserialize(txt);
  }

  public static DateTime deserialize(final String txt) throws IOException
  {
    // Try casting to a long first
    try
    {
      final Long dt = Long.valueOf(txt);
      return new DateTime(dt, DateTimeZone.UTC);
    }
    catch (final NumberFormatException ignored) {}

    // If we have reached here then it isn't a long so should be a number

    int offset = 0;

    // Manually parse out the pieces to build a datetime
    final int year = Integer.parseInt(txt.substring(offset, 4 + offset));
    final int monthOfYear = Integer.parseInt(txt.substring(5 + offset, 7 + offset));
    final int dayOfMonth = Integer.parseInt(txt.substring(8 + offset, 10 + offset));
    final int hourOfDay = Integer.parseInt(txt.substring(11 + offset, 13 + offset));
    final int minuteOfHour = Integer.parseInt(txt.substring(14 + offset, 16 + offset));
    final int secondOfMinute = Integer.parseInt(txt.substring(17 + offset, 19 + offset));
    int millisOfSecond = 0;

    // There are three possibilities for how the datetime ends.  It might just stop at this point, in which case it is UTC,
    // it might be in ISO8601 format, or it might have a full timezone appended after a space separator

    int additionalHours = 0;
    int additionalMinutes = 0;

    if (txt.length() == 19 + offset)
    {
      // No more information
      return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, DateTimeZone.UTC);
    }

    if (txt.charAt(19 + offset) == '.')
    {
      // Milliseconds have been added in the form .sss
      millisOfSecond = Integer.parseInt(txt.substring(20 + offset, 23 + offset));
      offset += 4;
    }

    if (txt.charAt(19 + offset) == '+')
    {
      additionalHours = -Integer.parseInt(txt.substring(20 + offset, 22 + offset));
      if (txt.charAt(22 + offset) == ':')
      {
        offset++;
      }
      additionalMinutes = -Integer.parseInt(txt.substring(22 + offset, 24 + offset));
      offset += 4;
    }
    else if (txt.charAt(19 + offset) == '-')
    {
      additionalHours = Integer.parseInt(txt.substring(20 + offset, 22 + offset));
      if (txt.charAt(22 + offset) == ':')
      {
        offset++;
      }
      additionalMinutes = Integer.parseInt(txt.substring(22 + offset, 24 + offset));
      offset += 3;
    }
    else if (txt.charAt(19 + offset) == 'Z')
    {
      // Just skip the UTC zone information as it doesn't do anything
      offset += 1;
    }

    if (txt.indexOf(' ') > 19)
    {
      return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond,
                          DateTimeZone.UTC).plusMinutes(additionalMinutes)
                                           .plusHours(additionalHours)
                                           .withZone(DateTimeZone.forID(txt.substring(txt.indexOf(' ') + 1)));
    }

    return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond,
                        DateTimeZone.UTC).plusMinutes(additionalMinutes).plusHours(additionalHours);
  }
}
