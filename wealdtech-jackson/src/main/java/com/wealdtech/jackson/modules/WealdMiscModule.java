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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.google.common.collect.Range;
import messaging.MessageObjects;

import java.net.InetSocketAddress;

/**
 * Custom serializers and deserializers for miscellaneous types.
 */
public class WealdMiscModule extends Module
{
  private static final transient String NAME = "WealdMiscModule";
  private transient Version version;

  @Override
  public String getModuleName()
  {
    return NAME;
  }

  @Override
  public Version version()
  {
    if (this.version == null)
    {
      this.version = new Version(1, 0, 0, null, "com.wealdtech", "utils");
    }
    return this.version;
  }

  @Override
  public void setupModule(final SetupContext context)
  {
    // Serializers and deserializers alter values
    final SimpleSerializers serializers = new SimpleSerializers();
    serializers.addSerializer(new InetSocketAddressSerializer());
    serializers.addSerializer(new MessageObjectsSerializer());
    serializers.addSerializer(new DateTimeRangeSerializer());

    final SimpleDeserializers deserializers = new SimpleDeserializers();
    deserializers.addDeserializer(InetSocketAddress.class, new InetSocketAddressDeserializer());
    deserializers.addDeserializer(MessageObjects.class, new MessageObjectsDeserializer());
    deserializers.addDeserializer(Range.class, new DateTimeRangeDeserializer());

    context.addSerializers(serializers);
    context.addDeserializers(deserializers);
  }
}
