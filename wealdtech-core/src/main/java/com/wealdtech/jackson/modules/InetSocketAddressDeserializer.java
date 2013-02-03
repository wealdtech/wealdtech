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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.google.common.base.Splitter;

public class InetSocketAddressDeserializer extends FromStringDeserializer<InetSocketAddress>
{
  private static final long serialVersionUID = -8771787639009660187L;

  public InetSocketAddressDeserializer()
  {
    super(InetSocketAddress.class);
  }

  @Override
  protected InetSocketAddress _deserialize(String value, DeserializationContext ctxt) throws IOException, JsonProcessingException
  {
    // TODO check for malformed values
    Iterator<String> vals = Splitter.on(':').split(value).iterator();
    final String host = vals.next();
    final int port = Integer.parseInt(vals.next());
    return new InetSocketAddress(host, port);
  }

  // TODO
//  @Override
//  protected InetSocketAddress _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException, JsonProcessingException
//  {
//    if (ob instanceof byte[])
//    {
//      byte[] bytes = (byte[]) ob;
//      if (bytes.length != 8)
//      {
//        ctxt.mappingException("Can only construct WIDs from 8 byte arrays");
//      }
//      DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
//      return new WID<>(in.readLong());
//    }
//    super._deserializeEmbedded(ob,  ctxt);
//    return null;
//  }
}
