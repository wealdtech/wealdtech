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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Custom serializer for Joda Interval objects.
 * This serializer presents Joda Interval objects as complex objects.
 * It provides a 'startdatetime' field which provides the DateTime in ISO 8601
 * format and also provides a 'starttimezone' field which provides the timezone
 * of the DateTime in such a format that the entire DateTime can be recreated
 * with no loss of information.  Similar fields are provided for 'enddatetime'
 * and 'endtimezone', resulting in a complete interval.
 */
public class IntervalSerializer extends StdSerializer<Interval>
{
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ ZZZ");

  public IntervalSerializer()
  {
    super(Interval.class, true);
  }

  @Override
  public void serialize(final Interval value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    gen.writeStartObject();
    gen.writeStringField("startdatetime", formatter.print(value.getStart()));
    gen.writeStringField("starttimezone", value.getStart().getZone().toString());
    gen.writeStringField("enddatetime", formatter.print(value.getEnd()));
    gen.writeStringField("endtimezone", value.getEnd().getZone().toString());
    gen.writeEndObject();
  }
}
