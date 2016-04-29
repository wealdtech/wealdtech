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
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DateTimeElementDefinitionTest
{
  @Test
  public void testSimpleElement()
  {
    final ResultGenerator<DateTime> generator = new ResultGenerator<DateTime>(){
      @Override
      public ImmutableList<Result> generate(final ImmutableList<String> inputs, final ResultValidator<DateTime> validator)
      {
        final ImmutableList.Builder<Result> resultsB = ImmutableList.builder();
        if (inputs != null)
        {
          for (final String input : inputs)
          {
            final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            final DateTime dt = formatter.parseDateTime(input).withZone(DateTimeZone.UTC);
            final boolean valid = validator == null || validator.validate(input, dt);
            resultsB.add(Result.builder().value(dt).state(valid ? State.VALID : State.INVALID).build());
          }
        }
        return resultsB.build();
      }
    };

    final ElementDefinition<DateTime> elementDefinition = new ElementDefinition<>("when", true, null, generator, null);

    final Definition definition = new Definition(ImmutableList.of(elementDefinition));

    final ImmutableMap<String, ImmutableList<String>> inputs = ImmutableMap.of("when", ImmutableList.of("2015-06-05T12:00:00Z"));

    final ResultSet resultSet = ResultSet.fromDefinition(definition, inputs);
    final Element element = resultSet.obtainElement("when");

    assertNotNull(element);
    assertEquals(element.getResults().get(0).getState(), State.VALID);
    assertEquals(element.getResults().get(0).getValue(DateTime.class).get(), new DateTime(2015, 6, 5, 12, 0, 0, DateTimeZone.UTC));
  }
}
