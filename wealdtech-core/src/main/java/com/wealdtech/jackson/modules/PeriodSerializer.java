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

import java.io.IOException;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Custom serializer for Joda Period objects.
 * This serializer presents Joda Period objects in ISO 8601 format.
 */
public class PeriodSerializer extends StdSerializer<Period>
{
  private static PeriodFormatter formatter = ISOPeriodFormat.standard();

  public PeriodSerializer()
  {
    super(Period.class, true);
  }

  @Override
  public void serialize(final Period value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    gen.writeString(formatter.print(value));
  }
}
