package com.wealdtech.jackson.modules;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.TriVal;


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
}
