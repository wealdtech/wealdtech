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

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

/**
 *
 */
public class RangeKeyDeserializer extends KeyDeserializer
{
  private final JavaType _referenceType;

  public RangeKeyDeserializer(final JavaType keyType)
  {
    super();
    _referenceType = keyType.containedType(0) == null ? keyType : keyType.containedType(0);
  }

  @Override
  public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
  {
    if (key.length() == 0)
    {
      return null;
    }
    final Class<?> cls = _referenceType.getRawClass();
    if (Integer.class.isAssignableFrom(cls))
    {
      return new IntegerRangeDeserializer().deserializeFromString(key);
    }
    return null;
  }
}
