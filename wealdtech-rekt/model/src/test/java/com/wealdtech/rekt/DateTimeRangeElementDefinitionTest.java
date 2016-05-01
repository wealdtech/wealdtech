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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DateTimeRangeElementDefinitionTest
{
  @Test
  public void testSimpleElement()
  {
    final ResultGenerator<Range<DateTime>> generator = new ResultGenerator<Range<DateTime>>(){
      @Override
      public ImmutableList<Result> generate(final ImmutableList<String> inputs,
                                            final ResultValidator<Range<DateTime>> validator,
                                            final AdditionalInfo additionalInfo)
      {
        final ImmutableList.Builder<Result> resultsB = ImmutableList.builder();
        if (inputs != null)
        {
          for (final String input : inputs)
          {
            try
            {
              final Range<DateTime> result =
                  WealdMapper.getMapper().readValue("\"" + input + "\"", new TypeReference<Range<DateTime>>() {});
              final boolean valid = validator == null || validator.validate(input, result, null);
              resultsB.add(Result.builder().value(result).state(valid ? State.GOOD : State.INVALID).build());
            }
            catch (final IOException ioe)
            {
              resultsB.add(Result.builder().state(State.UNPARSEABLE).build());
            }
          }
        }
        return resultsB.build();
      }
    };

    final ElementDefinition<Range<DateTime>> elementDefinition = new ElementDefinition<>("duration", true, null, generator, null);

    final ElementDefinitionGroup definition = new ElementDefinitionGroup(ImmutableList.of(elementDefinition));

    final ImmutableMap<String, ImmutableList<String>> inputs = ImmutableMap.of("duration", ImmutableList.of("[2015-06-05T12:00:00Z,2015-06-05T14:00:00Z)"));

    final ResultSet resultSet = ResultSet.fromDefinition(definition, inputs);
    final Element element = resultSet.obtainElement("duration");

    assertNotNull(element);
    assertEquals(element.getResults().get(0).getState(), State.GOOD);
    assertEquals(element.getResults().get(0).getValue(new TypeReference<Range<DateTime>>() {}).get(),
                 Range.closedOpen(new DateTime(2015, 6, 5, 12, 0, 0, DateTimeZone.UTC),
                                  new DateTime(2015, 6, 5, 14, 0, 0, DateTimeZone.UTC)));
  }
}
