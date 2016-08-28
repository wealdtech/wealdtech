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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RangeDeserializer extends StdDeserializer<Range<?>>
{
  private static final Logger LOG = LoggerFactory.getLogger(RangeDeserializer.class);

  private final JavaType _referenceType;

  public RangeDeserializer(JavaType valueType)
  {
    super(valueType);
    _referenceType = valueType.containedType(0);
  }

  @Override
  public Range<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
  {
    final Class<?> cls = _referenceType.getRawClass();
    if (DateTime.class.isAssignableFrom(cls))
    {
      return new DateTimeRangeDeserializer().deserialize(jp, ctxt);
    }
    else if (Integer.class.isAssignableFrom(cls))
    {
      return new IntegerRangeDeserializer().deserialize(jp, ctxt);
    }
    else
    {
      throw new IllegalArgumentException("Ranges of type " + cls.toString() + " not supported");
    }
  }
}