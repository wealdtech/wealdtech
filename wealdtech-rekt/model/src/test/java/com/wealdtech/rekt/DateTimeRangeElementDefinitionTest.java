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

import javax.annotation.Nullable;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DateTimeRangeElementDefinitionTest
{
  @Test
  public void testSimpleElement()
  {
    final Parser<Range<DateTime>> parser = new Parser<Range<DateTime>>()
    {
      @Override
      public ImmutableList<Range<DateTime>> parse(@Nullable final String input)
      {
        final ImmutableList.Builder<Range<DateTime>> resultsB = ImmutableList.builder();
        if (input != null)
        {
          try
          {
            final Range<DateTime> result = WealdMapper.getMapper().readValue("\"" + input + "\"", new TypeReference<Range<DateTime>>() {});
            resultsB.add(result);
          }
          catch (final IOException ioe)
          {
            return null;
          }
        }
        return resultsB.build();
      }
    };

    final ValueDefinition<Range<DateTime>> elementDefinition = new ValueDefinition<>("duration", parser, true, null, null);

    final Definition definition = new Definition(ImmutableList.of(elementDefinition));

    final ImmutableMap<String, String> inputs = ImmutableMap.of("duration", "[2015-06-05T12:00:00Z,2015-06-05T14:00:00Z)");

    final ValueSet result = ValueSet.fromDefinition(definition, inputs);

    final Value<Range<DateTime>> parsedElement = result.getValue("duration");
    assertNotNull(parsedElement);
    assertEquals(parsedElement.getState(), Value.State.VALID);
    assertEquals(parsedElement.getValue(), Range.closedOpen(new DateTime(2015, 6, 5, 12, 0, 0, DateTimeZone.UTC),
                                                            new DateTime(2015, 6, 5, 14, 0, 0, DateTimeZone.UTC)));
  }
}
