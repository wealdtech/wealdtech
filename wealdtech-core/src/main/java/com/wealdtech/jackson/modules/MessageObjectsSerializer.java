package com.wealdtech.jackson.modules;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.utils.messaging.MessageObjects;

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
                        final SerializerProvider provider) throws JsonProcessingException, IOException
  {
    jgen.writeStartObject();
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
}