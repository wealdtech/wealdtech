/*
 *    Copyright 2012 Weald Technology Trading Limited
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

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.wealdtech.utils.CompoundWID;

import java.io.IOException;

public class CompoundWIDDeserializer extends FromStringDeserializer<CompoundWID<?, ?>>
{
  private static final long serialVersionUID = -8771787639009660187L;

  public CompoundWIDDeserializer()
  {
    super(CompoundWID.class);
  }

  @Override
  protected CompoundWID<?, ?> _deserialize(String value, DeserializationContext ctxt) throws IOException
  {
    return CompoundWID.fromString(value);
  }
}
