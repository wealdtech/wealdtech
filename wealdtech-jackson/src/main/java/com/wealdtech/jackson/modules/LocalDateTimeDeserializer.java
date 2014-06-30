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
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);

  @Override
  public LocalDateTime deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException
  {
    final ObjectCodec oc = jsonParser.getCodec();
    final JsonNode node = oc.readTree(jsonParser);

    try
    {
      return formatter.parseDateTime(node.textValue()).withZone(DateTimeZone.UTC).toLocalDateTime();
    }
    catch (IllegalArgumentException iae)
    {
      LOGGER.warn("Attempt to deserialize invalid localdatetime {}", node.textValue());
      throw new IOException("Invalid localdatetime value \"" + node.textValue() + "\"", iae);
    }
  }
}
