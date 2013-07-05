package com.wealdtech.jackson.modules;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wealdtech.TriVal;

public class TriValDeserializer extends StdDeserializer<TriVal<?>>
{
  private static final long serialVersionUID = -2470822369451022310L;

  private final JavaType _referenceType;

  public TriValDeserializer(JavaType valueType)
  {
    super(valueType);
    _referenceType = valueType.containedType(0);
  }

  @Override
  public TriVal<?> getNullValue()
  {
    return TriVal.absent();
  }

  @Override
  public TriVal<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
  {
    if (jp.getCurrentToken() == JsonToken.VALUE_STRING && jp.getText().length() == 0)
    {
      return TriVal.clear();
    }
    Object reference = ctxt.findRootValueDeserializer(_referenceType).deserialize(jp, ctxt);
    return TriVal.of(reference);
  }
}
