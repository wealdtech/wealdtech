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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.wealdtech.TwoTuple;

/**
 * A simple input-by-input generator with optional validation
 */
public abstract class SimpleGenerator<T> implements ResultGenerator<T>
{
  @Override
  public ImmutableList<Result> generate(final ImmutableList<String> inputs,
                                        final ResultValidator<T> validator,
                                        final AdditionalInfo additionalInfo)
  {
    final ImmutableList.Builder<Result> resultsB = ImmutableList.builder();
    if (inputs != null)
    {
      for (final String input : inputs)
      {
        if (Strings.isNullOrEmpty(input))
        {
          resultsB.add(Result.builder().state(State.MISSING).build());
        }
        else
        {
          final TwoTuple<T, ImmutableList<T>> inputResults = generate(input, additionalInfo);
          if (inputResults.getS() == null)
          {
            if (inputResults.getT() == null)
            {
              // Input is unparseable
              resultsB.add(Result.builder().state(State.UNPARSEABLE).build());
            }
            else
            {
              // Input is ambiguous
              resultsB.add(Result.builder().potentialValues(inputResults.getT()).state(State.AMBIGUOUS).build());
            }
          }
          else
          {
            if (validator == null)
            {
              // No validator so the result is good
              resultsB.add(Result.builder().value(inputResults.getS()).state(State.GOOD).build());
            }
            else
            {
              // Validator so the result depends on the result of validation
              final State state = validator.validate(input, inputResults.getS(), additionalInfo) ? State.GOOD : State.INVALID;
              resultsB.add(Result.builder().value(inputResults.getS()).state(state).build());
            }
          }
        }
      }
    }
    return resultsB.build();
  }

  /**
   * Generate results given an input
   * @param input the input from which to generate the result
   * @return a two tuple of information.  The first element in the tuple should only be provided if there is a single unambiguous
   * result.  The second element in the tuple should only be provided if there are multiple ambiguous results.
   */
  public abstract TwoTuple<T, ImmutableList<T>> generate(final String input, final AdditionalInfo additionalInfo);
}
