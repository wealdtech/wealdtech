/*
 *    Copyright 2013 Weald Technology Trading Limited
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wealdtech.DataError;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.messaging.MessageObjects;

public class MessageObjectsDeserializer extends StdDeserializer<MessageObjects<?>>
{
  private static final long serialVersionUID = -8937155316970465927L;
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageObjectsDeserializer.class);

  public MessageObjectsDeserializer()
  {
    super(MessageObjects.class);
  }

  @Override
  public MessageObjects<? extends Object> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
  {
    // This assumes a strict JSON format of _type, followed by prior and current (if they exist)
    jp.nextToken();
    String fieldname = jp.getCurrentName();
    if (!"_type".equals(fieldname))
    {
      throw new IOException("Unexpected key \"" + fieldname + "\"; expected _type");
    }
    jp.nextToken();
    final String typestr = jp.getText();
    Class<? extends Object> objclass;
    try
    {
      objclass = Class.forName(typestr);
    }
    catch (ClassNotFoundException cnfe)
    {
      LOGGER.error("MessageObjects has unknown class: \"" + typestr + "\"");
      throw new IOException("MessageObjects has unknown class", cnfe);
    }

    // Now that we have the type we can deserialize the objects
    jp.nextToken();
    fieldname = jp.getCurrentName();

    Object prior = null;
    if ("prior".equals(fieldname))
    {
      jp.nextToken();
      prior = WealdMapper.getMapper().readValue(jp, objclass);
      jp.nextToken();
      fieldname = jp.getCurrentName();
    }

    Object current = null;
    if ("current".equals(fieldname))
    {
      jp.nextToken();
      current = WealdMapper.getMapper().readValue(jp, objclass);
    }

    // And build our return object
    try
    {
      return new MessageObjects<>(prior, current);
    }
    catch (DataError de)
    {
      LOGGER.error("Failed to instantiate MessageObjects: \"" + de.getLocalizedMessage() + "\"");
      throw new IOException("Failed to instantiate MessageObjects", de);
    }
  }
}