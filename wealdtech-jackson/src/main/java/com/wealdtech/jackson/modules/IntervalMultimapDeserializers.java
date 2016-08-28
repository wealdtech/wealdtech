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

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.wealdtech.collect.IntervalMultimap;

public class IntervalMultimapDeserializers extends Deserializers.Base
{
  @Override
  public JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType type,
                                                     final DeserializationConfig config,
                                                     final BeanDescription beanDesc,
                                                     final KeyDeserializer keyDeserializer,
                                                     final TypeDeserializer elementTypeDeserializer,
                                                     final JsonDeserializer<?> elementDeserializer) throws JsonMappingException
  {
    if (type.hasRawClass(IntervalMultimap.class))
    {
      return new IntervalMultimapDeserializer<>(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }
    return null;
  }
}
