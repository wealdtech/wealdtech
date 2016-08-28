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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class IntegerRangeDeserializer extends JsonDeserializer<Range<Integer>>
{
  private static final Logger LOG = LoggerFactory.getLogger(IntegerRangeDeserializer.class);

  private static final String NEGATIVE_INFINITY = "-∞";
  private static final String POSITIVE_INFINITY = "+∞";

  private static final char WEALD_SPLITTER_CHAR = ',';
  private static final Splitter WEALD_SPLITTER = Splitter.on(WEALD_SPLITTER_CHAR);

  private static final char GUAVA_SPLITTER_CHAR = '‥';
  private static final Splitter GUAVA_SPLITTER = Splitter.on(GUAVA_SPLITTER_CHAR);

  private Class<?> targetClass;

  @Override
  public Range<Integer> deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    final JsonToken token = jp.getCurrentToken();
      // Deserialize from string
      final String txt = jp.getText();
      if (txt == null)
      {
        return null;
      }
      return deserializeFromString(txt);
  }

  public static Range<Integer> deserializeFromString(final String txt) throws IOException
  {
    final int txtLen = txt.length();

    final int lowerEndpointChar;
    boolean lowerClosed;
    if (txt.charAt(0) == '[')
    {
      lowerEndpointChar = 1;
      lowerClosed = true;
    }
    else if (txt.charAt(0) == '(')
    {
      lowerEndpointChar = 1;
      lowerClosed = false;
    }
    else if (txt.charAt(0) >= '0' && txt.charAt(0) <= '9')
    {
      // Lazy version
      lowerEndpointChar = 0;
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
    else if (lowerEndpointChar == 0)
    {
      upperClosed = false;
    }
    else
    {
      throw new DataError.Bad("Unexpected last character in range \"" + txt + "\"");
    }

    final Iterator<String> endpoints;
    if (txt.indexOf(WEALD_SPLITTER_CHAR) != -1)
    {
      endpoints = WEALD_SPLITTER.split(txt.substring(lowerEndpointChar, txtLen - lowerEndpointChar)).iterator();
    }
    else if (txt.indexOf(GUAVA_SPLITTER_CHAR) != -1)
    {
      endpoints = GUAVA_SPLITTER.split(txt.substring(lowerEndpointChar, txtLen - lowerEndpointChar)).iterator();
    }
    else
    {
      throw new DataError.Bad("Cannot find a range separator in range \"" + txt + "\"");
    }
    String lower = endpoints.next();
    String upper = endpoints.next();

    boolean lowerBound;
    final Integer lowerEndpoint;
    if (lower.equals(NEGATIVE_INFINITY))
    {
      lowerBound = false;
      lowerEndpoint = null;
    }
    else
    {
      lowerBound = true;
      lowerEndpoint = Integer.valueOf(lower);
    }

    boolean upperBound;
    final Integer upperEndpoint;
    if (upper.equals(POSITIVE_INFINITY))
    {
      upperBound = false;
      upperEndpoint = null;
    }
    else
    {
      upperBound = true;
      upperEndpoint = Integer.valueOf(upper);
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
        return Range.lessThan(upperEndpoint);
      }
      else
      {
        return Range.atMost(upperEndpoint);
      }
    }
    else if (upperBound == false)
    {
      // Lower present
      if (lowerClosed == true)
      {
        return Range.atLeast(lowerEndpoint);
      }
      else
      {
        return Range.greaterThan(lowerEndpoint);
      }
    }
    else
    {
      // Both present
      if (lowerClosed == true)
      {
        if (upperClosed == true)
        {
          return Range.closed(lowerEndpoint, upperEndpoint);
        }
        else
        {
          return Range.closedOpen(lowerEndpoint, upperEndpoint);
        }
      }
      else
      {
        if (upperClosed == true)
        {
          return Range.openClosed(lowerEndpoint, upperEndpoint);
        }
        else
        {
          return Range.open(lowerEndpoint, upperEndpoint);
        }
      }
    }
  }
}
