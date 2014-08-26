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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class InetSocketAddressSerializer extends StdSerializer<InetSocketAddress>
{
  public InetSocketAddressSerializer()
  {
    super(InetSocketAddress.class, true);
  }

  @Override
  public void serialize(final InetSocketAddress value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException
  {
    jgen.writeString(value.getHostName() + ":" + value.getPort());
  }

  @Override
  public void serializeWithType(final InetSocketAddress value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer)
      throws IOException, JsonProcessingException
  {
    typeSer.writeTypePrefixForScalar(value, jgen, InetSocketAddress.class);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
