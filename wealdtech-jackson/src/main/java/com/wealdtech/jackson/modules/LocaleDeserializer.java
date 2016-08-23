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
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * Due to an issue with Jackson it does not handle deserialization of locales in the style "en-GB".  This
 * deserializer handles that style
 *
 * This class can be removed as/when https://github.com/FasterXML/jackson-databind/issues/1344 is fixed
 */
public class LocaleDeserializer extends JsonDeserializer<Locale>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LocaleDeserializer.class);

  private static final char STANDARD_SPLITTER_CHAR = '-';
  private static final char UNDERSCORE_SPLITTER_CHAR = '_';

  @Override
  public Locale deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws IOException
  {
    String txt = jp.getText();
    if (txt == null)
    {
      return null;
    }

    int separator = txt.indexOf(STANDARD_SPLITTER_CHAR);
    if (separator == -1)
    {
      separator = txt.indexOf(UNDERSCORE_SPLITTER_CHAR);
    }

    if (separator == -1)
    {
      // No separator so return
      return new Locale(txt);
    }

    final String language = txt.substring(0, separator);

    txt = txt.substring(separator+1);

    separator = txt.indexOf(STANDARD_SPLITTER_CHAR);
    if (separator == -1)
    {
      separator = txt.indexOf(UNDERSCORE_SPLITTER_CHAR);
    }

    if (separator == -1)
    {
      // No further separator so return
      return new Locale(language, txt);
    }

    final String country = txt.substring(0, separator);

    txt = txt.substring(separator+1);

    return new Locale(language, country, txt);
  }
}
