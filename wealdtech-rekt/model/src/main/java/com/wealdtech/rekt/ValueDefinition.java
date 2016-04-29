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

import javax.annotation.Nullable;

public class ValueDefinition<T>
{
  private final String name;
  private final boolean mandatory;
  private final Parser<T> parser;
  private final Validator<T> validator;
  private final T defaultValue;

  public ValueDefinition(final String name,
                         final Parser<T> parser,
                         final boolean mandatory,
                         @Nullable Validator<T> validator,
                         @Nullable final T defaultValue)
  {
    this.name = name;
    this.parser = parser;
    this.mandatory = mandatory;
    this.validator = validator;
    this.defaultValue = defaultValue;
  }

  public String getName()
  {
    return name;
  }

  public Parser<T> getParser()
  {
    return parser;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  @Nullable
  public Validator<T> getValidator()
  {
    return validator;
  }

  @Nullable
  public T getDefaultValue()
  {
    return defaultValue;
  }
}
