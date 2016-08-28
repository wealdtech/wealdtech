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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Range;
import com.wealdtech.collect.IntervalMultimap;

import java.io.IOException;

public class IntervalMultimapDeserializer<T extends Comparable<T>, U> extends JsonDeserializer<IntervalMultimap<T, U>> implements
    ContextualDeserializer
{

  protected MapLikeType mapType;
  protected KeyDeserializer keyDeserializer;
  protected TypeDeserializer typeDeserializer;
  protected JsonDeserializer<?> elementDeserializer;

  public IntervalMultimapDeserializer(final MapLikeType mapType,
                                      final KeyDeserializer keyDeserializer,
                                      final TypeDeserializer typeDeserializer,
                                      final JsonDeserializer<?> elementDeserializer)
  {
    this.mapType = mapType;
    this.keyDeserializer = keyDeserializer;
    this.typeDeserializer = typeDeserializer;
    this.elementDeserializer = elementDeserializer;
  }

  @Override
  public IntervalMultimap deserialize(final JsonParser jp, final DeserializationContext ctxt) throws
                                                                                             IOException,
                                                                                             JsonProcessingException
  {
    final IntervalMultimap<T, U> map = new IntervalMultimap<>();

    expect(jp, JsonToken.START_OBJECT);

    while (jp.nextToken() != JsonToken.END_OBJECT) {
        final Range<T> key;
         key = (Range<T>)keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);

        jp.nextToken();
        expect(jp, JsonToken.START_ARRAY);

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            final U value;
            if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
                value = null;
            } else {
                value = (U)elementDeserializer.deserialize(jp, ctxt);
            }
            map.put(key, value);
        }
    }
    return map;
  }

  private void expect(JsonParser jp, JsonToken token) throws IOException {
      if (jp.getCurrentToken() != token) {
          throw new JsonMappingException(jp, "Expecting " + token + ", found " + jp.getCurrentToken(),
                                         jp.getCurrentLocation());
      }
  }

  @Override
  public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws
                                                                                                              JsonMappingException
  {
    if (keyDeserializer == null)
    {
      keyDeserializer = ctxt.findKeyDeserializer(mapType.getKeyType(), property);
    }
    if (elementDeserializer == null)
    {
      elementDeserializer = ctxt.findContextualValueDeserializer(mapType.getContentType(), property);
    }
    TypeDeserializer localTypeDeserializer = typeDeserializer == null ? null : typeDeserializer.forProperty(property);
    return new IntervalMultimapDeserializer<>(mapType, keyDeserializer, typeDeserializer, elementDeserializer);
  }
}
