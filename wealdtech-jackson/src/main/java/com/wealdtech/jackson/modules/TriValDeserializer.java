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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wealdtech.TriVal;

import java.io.IOException;

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
  public TriVal<?> getNullValue(final DeserializationContext context)
  {
    return TriVal.absent();
  }

  @Override
  public TriVal<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
  {
    if (jp.getCurrentToken() == JsonToken.VALUE_STRING && jp.getText().equals("__"))
    {
      return TriVal.clear();
    }
    Object reference = ctxt.findRootValueDeserializer(_referenceType).deserialize(jp, ctxt);
    return TriVal.of(reference);
  }
}
