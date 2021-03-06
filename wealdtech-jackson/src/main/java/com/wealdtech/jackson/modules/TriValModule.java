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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaTypeModifier;
import com.fasterxml.jackson.datatype.guava.PackageVersion;

public class TriValModule extends Module
{
  private static final String NAME = "TriValModule";

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
      context.addTypeModifier(new GuavaTypeModifier());
      context.addBeanSerializerModifier(new TriValBeanSerializerModifier());
  }
}
