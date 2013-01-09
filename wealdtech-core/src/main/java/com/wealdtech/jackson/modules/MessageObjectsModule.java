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