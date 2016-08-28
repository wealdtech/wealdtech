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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.joda.time.DateTime;

import java.io.IOException;

public class RangeSerializer extends StdSerializer<Range<?>>
{
  public RangeSerializer(final JavaType valueType)
  {
    super(valueType);
  }

  private static final String NEGATIVE_INFINITY = "-∞";
  private static final String POSITIVE_INFINITY = "+∞";

  @Override
  public void serialize(final Range<?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    final Class<?> cls;
    if (value.hasLowerBound())
    {
      cls = value.lowerEndpoint().getClass();
    }
    else if (value.hasUpperBound())
    {
      cls = value.upperEndpoint().getClass();
    }
    else
    {
      cls = Object.class;
    }

    if (DateTime.class.isAssignableFrom(cls))
    {
      new DateTimeRangeSerializer().serialize((Range<DateTime>)value, gen, provider);
    }
    else
    {

      if (value != null)
      {
        gen.writeRaw('"');
        if (value.hasLowerBound())
        {
          if (value.lowerBoundType().equals(BoundType.CLOSED))
          {
            gen.writeRaw('[');
          }
          else
          {
            gen.writeRaw('(');
          }
          provider.findTypedValueSerializer(value.lowerEndpoint().getClass(), true, null)
                  .serialize(value.lowerEndpoint(), gen, provider);
          //        provider.defaultSerializeValue(value.lowerEndpoint(), gen);
        }
        else
        {
          gen.writeRaw('(');
          gen.writeRaw(NEGATIVE_INFINITY);
        }
        gen.writeRaw(',');

        if (value.hasUpperBound())
        {
          provider.defaultSerializeValue(value.upperEndpoint(), gen);
          if (value.upperBoundType().equals(BoundType.CLOSED))
          {
            gen.writeRaw(']');
          }
          else
          {
            gen.writeRaw(')');
          }
        }
        else
        {
          gen.writeRaw(POSITIVE_INFINITY);
          gen.writeRaw(')');
        }
        gen.writeRaw('"');
      }
    }
  }

  @Override
  public void serializeWithType(final Range<?> value,
                                JsonGenerator jgen,
                                SerializerProvider provider,
                                TypeSerializer typeSer) throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, Range.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
