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
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class DateTimeRangeDeserializer extends JsonDeserializer<Range<DateTime>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeDeserializer.class);

  private static final String NEGATIVE_INFINITY = "-∞";
  private static final String POSITIVE_INFINITY = "+∞";

  private static final char WEALD_SPLITTER_CHAR = ',';
  private static final Splitter WEALD_SPLITTER = Splitter.on(WEALD_SPLITTER_CHAR);

  private static final char GUAVA_SPLITTER_CHAR = '‥';
  private static final Splitter GUAVA_SPLITTER = Splitter.on(GUAVA_SPLITTER_CHAR);

  private Class<?> targetClass;

  @Override
  public Range<DateTime> deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    final JsonToken token = jp.getCurrentToken();
    if (token == JsonToken.START_OBJECT)
    {
      // Deserialize from objects
      return deserializeFromObjects(jp, deserializationContext);
    }
    else
    {
      // Deserialize from string
      final String txt = jp.getText();
      if (txt == null)
      {
        return null;
      }
      return deserializeFromString(txt);
    }
  }

  public static Range<DateTime> deserializeFromObjects(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    DateTime from = null;
    DateTime to = null;

    final DateTimeDeserializer dtDeserializer = new DateTimeDeserializer();

    while (jp.nextToken() != JsonToken.END_OBJECT)
    {
      final String fieldName = jp.getCurrentName();
      jp.nextToken();
      if ("from".equals(fieldName))
      {
        from = dtDeserializer.deserialize(jp, deserializationContext);
      }
      else if ("to".equals(fieldName))
      {
        to = dtDeserializer.deserialize(jp, deserializationContext);
      }
    }

    if (from == null && to == null)
    {
      return Range.all();
    }
    if (from == null)
    {
      return Range.lessThan(to);
    }
    if (to == null)
    {
      return Range.atLeast(from);
    }
    return Range.closedOpen(from, to);
  }

  public static Range<DateTime> deserializeFromString(final String txt) throws IOException
  {
    final int txtLen = txt.length();

    final int firstDateChar;
    boolean lowerClosed;
    if (txt.charAt(0) == '[')
    {
      firstDateChar = 1;
      lowerClosed = true;
    }
    else if (txt.charAt(0) == '(')
    {
      firstDateChar = 1;
      lowerClosed = false;
    }
    else if (txt.charAt(0) >= '0' && txt.charAt(0) <= '9')
    {
      // Lazy version
      firstDateChar = 0;
      lowerClosed = true;
    }
    else
    {
      throw new DataError.Bad("Unexpected first character in range \"" + txt + "\"");
    }

    boolean upperClosed;
    if (txt.charAt(txtLen - 1) == ']')
    {
      upperClosed = true;
    }
    else if (txt.charAt(txtLen - 1) == ')')
    {
      upperClosed = false;
    }
    else if (firstDateChar == 0)
    {
      upperClosed = false;
    }
    else
    {
      throw new DataError.Bad("Unexpected last character in range \"" + txt + "\"");
    }

    final Iterator<String> dateTimes;
    if (txt.indexOf(WEALD_SPLITTER_CHAR) != -1)
    {
      dateTimes = WEALD_SPLITTER.split(txt.substring(firstDateChar, txtLen - firstDateChar)).iterator();
    }
    else if (txt.indexOf(GUAVA_SPLITTER_CHAR) != -1)
    {
      dateTimes = GUAVA_SPLITTER.split(txt.substring(firstDateChar, txtLen - firstDateChar)).iterator();
    }
    else
    {
      throw new DataError.Bad("Cannot find a range separator in range \"" + txt + "\"");
    }
    String start = dateTimes.next();
    String end = dateTimes.next();

    boolean lowerBound;
    final DateTime lowerPoint;
    if (start.equals(NEGATIVE_INFINITY))
    {
      lowerBound = false;
      lowerPoint = null;
    }
    else
    {
      lowerBound = true;
      lowerPoint = DateTimeDeserializer.deserialize(start);
    }

    boolean upperBound;
    final DateTime upperPoint;
    if (end.equals(POSITIVE_INFINITY))
    {
      upperBound = false;
      upperPoint = null;
    }
    else
    {
      upperBound = true;
      upperPoint = DateTimeDeserializer.deserialize(end);
    }

    if (lowerBound == false && upperBound == false)
    {
      return Range.all();
    }
    else if (lowerBound == false)
    {
      // Upper present
      if (upperClosed == true)
      {
        return Range.lessThan(upperPoint);
      }
      else
      {
        return Range.atMost(upperPoint);
      }
    }
    else if (upperBound == false)
    {
      // Lower present
      if (lowerClosed == true)
      {
        return Range.atLeast(lowerPoint);
      }
      else
      {
        return Range.greaterThan(lowerPoint);
      }
    }
    else
    {
      // Both present
      if (lowerClosed == true)
      {
        if (upperClosed == true)
        {
          return Range.closed(lowerPoint, upperPoint);
        }
        else
        {
          return Range.closedOpen(lowerPoint, upperPoint);
        }
      }
      else
      {
        if (upperClosed == true)
        {
          return Range.openClosed(lowerPoint, upperPoint);
        }
        else
        {
          return Range.open(lowerPoint, upperPoint);
        }
      }
    }
  }
}
