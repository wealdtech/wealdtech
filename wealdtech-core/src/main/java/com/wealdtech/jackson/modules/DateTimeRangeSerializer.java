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
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class DateTimeRangeSerializer extends StdSerializer<Range<DateTime>>
{
  private static final String NEGATIVE_INFINITY = "-∞";
  private static final String POSITIVE_INFINITY = "+∞";
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ ZZZ");

  public DateTimeRangeSerializer()
  {
    super(Range.class, true);
  }

  @Override
  public void serialize(final Range<DateTime> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    if (value != null)
    {
      final StringBuilder sb = new StringBuilder(64);
      if (value.hasLowerBound())
      {
        if (value.lowerBoundType().equals(BoundType.CLOSED))
        {
          sb.append('[');
        }
        else
        {
          sb.append('(');
        }
        sb.append(formatter.print(value.lowerEndpoint()));
      }
      else
      {
        sb.append('(');
        sb.append(NEGATIVE_INFINITY);
      }
      sb.append(',');

      if (value.hasUpperBound())
      {
        sb.append(formatter.print(value.upperEndpoint()));
        if (value.upperBoundType().equals(BoundType.CLOSED))
        {
          sb.append(']');
        }
        else
        {
          sb.append(')');
        }
      }
      else
      {
        sb.append(POSITIVE_INFINITY);
        sb.append(')');
      }

      gen.writeString(sb.toString());
    }
  }
}
