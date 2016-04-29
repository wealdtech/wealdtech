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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.DataError;

import javax.annotation.Nullable;

/**
 * A ValueSet provides details of a set of values
 */
public class ValueSet
{
  private final ImmutableList<Value<?>> values;

  public ValueSet(final ImmutableList<Value<?>> values)
  {
    this.values = values;
  }

  public static ValueSet fromDefinition(final Definition definition, final ImmutableMap<String, String> inputs)
  {
    final ImmutableList.Builder<Value<?>> valuesB = ImmutableList.builder();

    for (final ValueDefinition<?> valueDefinition : definition.getValueDefinitions())
    {

      final Value value =
          handleValueDefinition(valueDefinition, inputs.get(valueDefinition.getName()), valuesB);
      if (value != null)
      {
        valuesB.add(value);
      }
    }

    return new ValueSet(valuesB.build());
  }

  @Nullable
  private static <T> Value<T> handleValueDefinition(final ValueDefinition<T> valueDefinition,
                                                    final String input,
                                                    final ImmutableList.Builder<Value<?>> valuesB)
  {
    final Value<T> value;
    if (input == null || Objects.equal(input, ""))
    {
      if (valueDefinition.isMandatory())
      {
        throw new DataError.Bad("Missing mandatory value " + valueDefinition.getName());
      }
      value = null;
    }
    else
    {
      final ImmutableList<T> results = valueDefinition.getParser().parse(input);
      if (results.isEmpty())
      {
        value = new Value<>(valueDefinition.getName(), input, null, null, Value.State.NOT_PARSED);
      }
      else if (results.size() == 1)
      {
        if (valueDefinition.getValidator() == null)
        {
          value = new Value<>(valueDefinition.getName(), input, null, getResult(results, 0),
                              Value.State.VALID);
        }
        else
        {
          if (valueDefinition.getValidator().isValid(results.get(0)))
          {
            value =
                new Value<>(valueDefinition.getName(), input, null, results.get(0), Value.State.VALID);
          }
          else
          {
            value =
                new Value<>(valueDefinition.getName(), input, null, results.get(0), Value.State.INVALID);
          }

        }
      }
      else
      {
        value = new Value<>(valueDefinition.getName(), input, results, null, Value.State.AMBIGUOUS);
      }
    }
    return value;
  }

  private static <T> T getResult(final ImmutableList<T> list, final int entry)
  {
    return (T)list.get(entry);
  }

  @JsonIgnore
  @Nullable
  public <T> Value<T> getValue(final String name)
  {
    for (final Value<?> value : values)
    {
      if (Objects.equal(name, value.getName()))
      {
        return (Value<T>)value;
      }
    }
    return null;
  }

  public ImmutableList<Value<?>> getValues()
  {
    return values;
  }
}
