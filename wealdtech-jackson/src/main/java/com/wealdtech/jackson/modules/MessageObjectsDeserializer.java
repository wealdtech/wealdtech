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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wealdtech.DataError;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.RequestHint;
import com.wealdtech.utils.messaging.MessageObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageObjectsDeserializer extends StdDeserializer<MessageObjects<?>>
{
  private static final long serialVersionUID = -8937155316970465927L;
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageObjectsDeserializer.class);

  public MessageObjectsDeserializer()
  {
    super(MessageObjects.class);
  }

  @Override
  public MessageObjects<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
  {
    // This assumes a strict JSON format of userId and hint followed by _type followed by prior and current (if they exist)
    jp.nextToken();
    String fieldName = jp.getCurrentName();
    if (!"userid".equals(fieldName))
    {
      throw new IOException("Unexpected key \"" + fieldName + "\"; expected userid");
    }
    jp.nextToken();
    final Long userId = Long.parseLong(jp.getText());

    final RequestHint hint;
    jp.nextToken();
    fieldName = jp.getCurrentName();
    if ("hint".equals(fieldName))
    {
      // Hint is optional
      jp.nextToken();
      hint = WealdMapper.getMapper().readValue(jp, RequestHint.class);
      jp.nextToken();
    }
    else
    {
      hint = null;
    }


    fieldName = jp.getCurrentName();
    if (!"_type".equals(fieldName))
    {
      throw new IOException("Unexpected key \"" + fieldName + "\"; expected _type");
    }

    jp.nextToken();
    final String typeStr = jp.getText();
    Class<?> objClass;
    try
    {
      objClass = Class.forName(typeStr);
    }
    catch (ClassNotFoundException cnfe)
    {
      LOGGER.error("MessageObjects has unknown class: \"{}\"", typeStr);
      throw new IOException("MessageObjects has unknown class: \"" + typeStr + "\"", cnfe);
    }

    // Now that we have the type we can deserialize the objects
    jp.nextToken();
    fieldName = jp.getCurrentName();

    Object prior = null;
    if ("prior".equals(fieldName))
    {
      jp.nextToken();
      prior = WealdMapper.getMapper().readValue(jp, objClass);
      jp.nextToken();
      fieldName = jp.getCurrentName();
    }

    Object current = null;
    if ("current".equals(fieldName))
    {
      jp.nextToken();
      current = WealdMapper.getMapper().readValue(jp, objClass);
    }

    // And build our return object
    try
    {
      return new MessageObjects<>(userId, hint, prior, current);
    }
    catch (DataError de)
    {
      LOGGER.error("Failed to instantiate MessageObjects: \"" + de.getLocalizedMessage() + "\"");
      throw new IOException("Failed to instantiate MessageObjects", de);
    }
  }
}