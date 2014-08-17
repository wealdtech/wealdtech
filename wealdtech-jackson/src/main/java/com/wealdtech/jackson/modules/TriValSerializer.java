package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.TriVal;

import java.io.IOException;


public class TriValSerializer extends StdSerializer<TriVal<?>>
{
  public TriValSerializer(JavaType type)
  {
    super(type);
  }

  @Override
  public void serialize(TriVal<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException
  {
    if (value.isAbsent())
    {
      provider.defaultSerializeNull(jgen);
    }
    else if (value.isClear())
    {
      provider.defaultSerializeValue("", jgen);
    }
    else
    {
      provider.defaultSerializeValue(value.get(), jgen);
    }
  }

  @Override
  public void serializeWithType(final TriVal<?> value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, TriVal.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
