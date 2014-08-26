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

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class IntervalDeserializer extends JsonDeserializer<Interval>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(IntervalDeserializer.class);
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
  private static DateTimeZone utczone = DateTimeZone.forID("UTC");

  @Override
  public Interval deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException
  {
    final ObjectCodec oc = jsonParser.getCodec();
    final JsonNode node = oc.readTree(jsonParser);

    DateTime start = deserializeDateTime(node, "start");
    DateTime end = deserializeDateTime(node, "end");
    return new Interval(start, end);
  }

  private DateTime deserializeDateTime(final JsonNode node, final String prefix) throws IOException
  {
    final JsonNode datetimenode = node.get(prefix + "datetime");
    if (datetimenode == null)
    {
      LOGGER.warn("Attempt to deserialize malformed interval");
      throw new IOException("Invalid interval value");
    }
    final String datetime = datetimenode.textValue();
    // Obtain values
    final JsonNode tznode = node.get(prefix + "timezone");
    String timezone = null;
    if (tznode != null)
    {
      timezone = tznode.textValue();
    }

    DateTime result = null;
    try
    {
      if ((timezone == null) || ("UTC".equals(timezone)))
      {
        result = formatter.parseDateTime(datetime).withZone(utczone);
      }
      else
      {
        result = formatter.parseDateTime(datetime).withZone(DateTimeZone.forID(timezone));
      }
    }
    catch (IllegalArgumentException iae)
    {
      LOGGER.warn("Attempt to deserialize invalid interval {},{}", datetime, timezone);
      throw new IOException("Invalid datetime value", iae);
    }
    return result;
  }
}
