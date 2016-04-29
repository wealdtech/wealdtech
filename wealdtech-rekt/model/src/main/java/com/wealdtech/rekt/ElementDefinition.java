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

public class ElementDefinition<T>
{
  private final String name;
  private final boolean mandatory;
  private final T defaultValue;
  private final ResultGenerator<T> generator;
  private final ResultValidator<T> validator;

  public ElementDefinition(final String name,
                           final boolean mandatory,
                           final T defaultValue,
                           final ResultGenerator<T> generator,
                           final ResultValidator<T> validator)
  {
    this.name = name;
    this.mandatory = mandatory;
    this.defaultValue = defaultValue;
    this.generator = generator;
    this.validator = validator;
  }

  public String getName()
  {
    return name;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public T getDefaultValue()
  {
    return defaultValue;
  }

  public ResultGenerator<T> getGenerator()
  {
    return generator;
  }

  public ResultValidator<T> getValidator()
  {
    return validator;
  }
}
