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
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.wealdtech.utils.CompoundWID;

public class CompoundWIDKeyDeserializer extends StdKeyDeserializer
{
  private static final long serialVersionUID = -9206684533148783990L;

  public CompoundWIDKeyDeserializer()
  {
    super(CompoundWID.class);
  }

  @Override
  protected Object _parse(String key, DeserializationContext ctxt) throws Exception
  {
    return CompoundWID.fromString(key);
  }
}