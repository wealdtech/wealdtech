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
import com.wealdtech.utils.messaging.MessageObjects;

/**
 * Jackson configuration for message objects.
 */
public class MessageObjectsModule extends Module
{
  private final transient static String NAME = "MessageObjectsModule";
  private transient Version version;

  @Override
  public String getModuleName()
  {
    return NAME;
  }

  @Override
  public Version version()
  {
    if (version == null)
    {
      version = new Version(1, 0, 0, null, "com.wealdtech", "utils");
    }
    return version;
  }

  @Override
  public void setupModule(final SetupContext context)
  {
    // Serializers and deserializers alter values
    final SimpleSerializers serializers = new SimpleSerializers();
    final SimpleDeserializers deserializers = new SimpleDeserializers();
    serializers.addSerializer(new MessageObjectsSerializer());
    deserializers.addDeserializer((Class<MessageObjects<?>>)(Class<?>)MessageObjects.class, new MessageObjectsDeserializer());

    context.addSerializers(serializers);
    context.addDeserializers(deserializers);
  }
}