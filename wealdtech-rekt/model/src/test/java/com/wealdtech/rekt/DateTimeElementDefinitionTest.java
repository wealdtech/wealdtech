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

import javax.annotation.Nullable;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DateTimeElementDefinitionTest
{
  @Test
  public void testSimpleElement()
  {
    final Parser<DateTime> parser = new Parser<DateTime>()
    {
      @Override
      public ImmutableList<DateTime> parse(@Nullable final String input)
      {
        final ImmutableList.Builder<DateTime> resultsB = ImmutableList.builder();
        if (input != null)
        {
          final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
          final DateTime dt = formatter.parseDateTime(input).withZone(DateTimeZone.UTC);
          resultsB.add(dt);
        }
        return resultsB.build();
      }
    };

    final ValueDefinition<DateTime> elementDefinition = new ValueDefinition<>("when", parser, true, null, null);

    final Definition definition = new Definition(ImmutableList.of(elementDefinition));

    final ImmutableMap<String, String> inputs = ImmutableMap.of("when", "2015-06-05T12:00:00Z");

    final ValueSet result = ValueSet.fromDefinition(definition, inputs);

    final Value<DateTime> parsedElement = result.getValue("when");
    assertNotNull(parsedElement);
    assertEquals(parsedElement.getState(), Value.State.VALID);
    assertEquals(parsedElement.getValue(), new DateTime(2015, 6, 5, 12, 0, 0, DateTimeZone.UTC));
  }
}
