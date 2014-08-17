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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.utils.messaging.MessageObjects;

import java.io.IOException;

/**
 * Custom serializer for message objects.
 * This serializer provides type info as well as prior and current representations of the object.
 */
public class MessageObjectsSerializer extends StdSerializer<MessageObjects<? extends Object>>
{
  public MessageObjectsSerializer()
  {
    super (MessageObjects.class, true);
  }

  @Override
  public void serialize(final MessageObjects<? extends Object> mo,
                        final JsonGenerator jgen,
                        final SerializerProvider provider) throws IOException
  {
    jgen.writeStartObject();
    provider.defaultSerializeField("userid", mo.getUserId(), jgen);
    if (mo.getHint() != null)
    {
      provider.defaultSerializeField("hint", mo.getHint(), jgen);
    }
    provider.defaultSerializeField("_type", mo.getType().getName(), jgen);
    if (mo.getPrior() != null)
    {
      // TODO Obey NON_NULL/NON_EMPTY
      jgen.writeObjectField("prior", mo.getPrior());
    }
    if (mo.getCurrent() != null)
    {
      jgen.writeObjectField("current", mo.getCurrent());
    }
    jgen.writeEndObject();
  }

  @Override
  public void serializeWithType(final MessageObjects<? extends Object> value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, MessageObjects.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}