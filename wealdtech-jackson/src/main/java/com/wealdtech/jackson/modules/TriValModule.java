package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.MultimapTypeModifier;
import com.fasterxml.jackson.datatype.guava.PackageVersion;

public class TriValModule extends Module
{
  private final String NAME = "TriValModule";

  public TriValModule()
  {
    super();
  }

  @Override
  public String getModuleName()
  {
    return NAME;
  }

  @Override
  public Version version()
  {
    return PackageVersion.VERSION;
  }

  @Override
  public void setupModule(final SetupContext context)
  {
      context.addDeserializers(new TriValDeserializers());
      context.addSerializers(new TriValSerializers());
      context.addTypeModifier(new MultimapTypeModifier());
      context.addBeanSerializerModifier(new TriValBeanSerializerModifier());
  }
}
