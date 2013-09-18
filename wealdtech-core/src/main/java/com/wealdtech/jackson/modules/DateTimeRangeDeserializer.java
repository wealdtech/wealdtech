/*
 *    Copyright 2012 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

class DateTimeRangeDeserializer extends JsonDeserializer<Range<DateTime>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeDeserializer.class);
  private static final String NEGATIVE_INFINITY = "-∞";
  private static final String POSITIVE_INFINITY = "+∞";
  private static final char TWODOT = '\u2025';
  private static final Splitter TWODOT_SPLITTER = Splitter.on(TWODOT);
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
  private static DateTimeZone utczone = DateTimeZone.forID("UTC");

  private Class<?> targetClass;

  @Override
  public Range<DateTime> deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    final String txt = jp.getText();
    if (txt == null)
    {
      return null;
    }
    final int txtLen = txt.length();

    boolean lowerClosed;
    if (txt.charAt(0) == '[')
    {
      lowerClosed = true;
    }
    else if (txt.charAt(0) == '(')
    {
      lowerClosed = false;
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
    else
    {
      throw new DataError.Bad("Unexpected last character in range \"" + txt + "\"");
    }


    final Iterator<String> dateTimes = TWODOT_SPLITTER.split(txt.substring(1, txtLen - 1)).iterator();
    String start = dateTimes.next();
    String end = dateTimes.next();

    boolean lowerBound;
    DateTime lowerPoint;
    if (start.equals(NEGATIVE_INFINITY))
    {
      lowerBound = false;
      lowerPoint = null;
    }
    else
    {
      lowerBound = true;
      lowerPoint = formatter.parseDateTime(start);
    }

    boolean upperBound;
    DateTime upperPoint;
    if (end.equals(POSITIVE_INFINITY))
    {
      upperBound = false;
      upperPoint = null;
    }
    else
    {
      upperBound = true;
      upperPoint = formatter.parseDateTime(end);
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



//    final ObjectCodec oc = jp.getCodec();
//    final JsonNode node = oc.readTree(jp);
//
//    final JsonNode datetimenode = node.get("datetime");
//    if (datetimenode == null)
//    {
//      LOGGER.warn("Attempt to deserialize malformed datetime");
//      throw new IOException("Invalid datettime value");
//    }
//    final String datetime = datetimenode.textValue();
//    // Obtain values
//    final JsonNode tznode = node.get("timezone");
//    String timezone = null;
//    if (tznode != null)
//    {
//      timezone = tznode.textValue();
//    }
//
//    DateTime result = null;
//    try
//    {
//      if ((timezone == null) || ("UTC".equals(timezone)))
//      {
//        result = formatter.parseDateTime(datetime).withZone(utczone);
//      }
//      else

//      {
//        result = formatter.parseDateTime(datetime).withZone(DateTimeZone.forID(timezone));
//      }
//    }
//    catch (IllegalArgumentException iae)
//    {
//      LOGGER.warn("Attempt to deserialize invalid datetime {},{}", datetime, timezone);
//      throw new IOException("Invalid datetime value \"" + datetime + "," + timezone + "\"", iae);
//    }
//    return result;
}
