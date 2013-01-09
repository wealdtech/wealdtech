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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class PeriodDeserializer extends JsonDeserializer<Period>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PeriodDeserializer.class);
  private static PeriodFormatter formatter = ISOPeriodFormat.standard();

  @Override
  public Period deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException
  {
    final ObjectCodec oc = jsonParser.getCodec();
    final JsonNode node = oc.readTree(jsonParser);

    Period result = null;
    try
    {
      result = formatter.parsePeriod(node.textValue());
    }
    catch (IllegalArgumentException iae)
    {
      LOGGER.warn("Attempt to deserialize invalid period {}", node.textValue());
      throw new IOException("Invalid period value", iae);
    }
    return result;
  }
}
