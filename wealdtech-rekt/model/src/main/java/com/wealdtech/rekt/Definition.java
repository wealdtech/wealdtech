/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.rekt;

import com.google.common.collect.ImmutableList;

public class Definition
{
  private final ImmutableList<? extends ElementDefinition<?>> definitions;

  public Definition(final ImmutableList<? extends ElementDefinition<?>> definitions)
  {
    this.definitions = definitions;
  }

  public ImmutableList<? extends ElementDefinition<?>> getElementDefinitions()
  {
    return definitions;
  }
}
