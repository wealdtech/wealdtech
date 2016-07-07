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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ResultSetTest
{
  @Test
  public void testSerialization()
  {
    final ImmutableList.Builder<Element> elementsB = ImmutableList.builder();
    elementsB.add(Element.builder()
                         .name("datetimes")
                         .inputs(ImmutableList.of("2015-05-06T14:00:00Z", "2015-05-06T16:00:00Z"))
                         .results(ImmutableList.of(Result.builder()
                                                         .value(new DateTime(2015, 5, 6, 14, 0, 0, DateTimeZone.UTC))
                                                         .state(State.GOOD)
                                                         .build(), Result.builder()
                                                                         .value(
                                                                             new DateTime(2015, 5, 6, 16, 0, 0, DateTimeZone.UTC))
                                                                         .state(State.GOOD)
                                                                         .build()))
                         .build());

    final ResultSet resultSet = ResultSet.builder().state(State.GOOD).elements(elementsB.build()).build();
    assertEquals(WObject.serialize(resultSet), "{\"elements\":[{\"inputs\":[\"2015-05-06T14:00:00Z\",\"2015-05-06T16:00:00Z\"],\"name\":\"datetimes\",\"results\":[{\"state\":\"Good\",\"value\":{\"timestamp\":1430920800000}},{\"state\":\"Good\",\"value\":{\"timestamp\":1430928000000}}]}],\"state\":\"Good\"}");
  }

  public static class Person
  {
    private final String name;
    private final String email;

    @JsonCreator
    public Person(@JsonProperty("name")final String name, @JsonProperty("email") final String email)
    {
      this.name = name;
      this.email = email;
    }

    @Override
    public boolean equals(final Object o)
    {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final Person person = (Person)o;
      return Objects.equal(name, person.name) && Objects.equal(email, person.email);
    }

    @Override
    public int hashCode()
    {
      return Objects.hashCode(name, email);
    }
  }

  @Test
  public void testSerializationPotentials()
  {
    final ImmutableList.Builder<Element> elementsB = ImmutableList.builder();
    elementsB.add(Element.builder()
                         .name("people")
                         .inputs(ImmutableList.of("Alice", "Bob"))
                         .results(ImmutableList.of(Result.builder()
                                                         .potentialValues(ImmutableList.of(new Person("Alice Jones", "aj@test.com"),
                                                                                           new Person("Alice Smith",
                                                                                                      "as@test.com")))
                                                         .state(State.AMBIGUOUS)
                                                         .build(),
                                                   Result.builder()
                                                         .potentialValues(ImmutableList.of(new Person("Bob Jones", "bj@test.com"),
                                                                                           new Person("Bob Smith",
                                                                                                      "bs@test.com")))
                                                         .state(State.AMBIGUOUS)
                                                         .build()))
                         .build());

    final ResultSet resultSet = ResultSet.builder().state(State.AMBIGUOUS).elements(elementsB.build()).build();
    assertEquals(WObject.serialize(resultSet), "{\"elements\":[{\"inputs\":[\"Alice\",\"Bob\"],\"name\":\"people\",\"results\":[{\"potentialvalues\":[{\"name\":\"Alice Jones\",\"email\":\"aj@test.com\"},{\"name\":\"Alice Smith\",\"email\":\"as@test.com\"}],\"state\":\"Ambiguous\"},{\"potentialvalues\":[{\"name\":\"Bob Jones\",\"email\":\"bj@test.com\"},{\"name\":\"Bob Smith\",\"email\":\"bs@test.com\"}],\"state\":\"Ambiguous\"}]}],\"state\":\"Ambiguous\"}");
  }

  @Test
  public void testDeserialization()
  {
    final String ser = "{\"elements\":[{\"inputs\":[\"2015-05-06T14:00:00Z\",\"2015-05-06T16:00:00Z\"],\"name\":\"datetimes\",\"results\":[{\"state\":\"Valid\",\"value\":{\"timestamp\":1430920800000}},{\"state\":\"Valid\",\"value\":{\"timestamp\":1430928000000}}]}],\"state\":\"Valid\"}";

    final ResultSet resultSet = WObject.deserialize(ser, ResultSet.class);
    assertNotNull(resultSet);
    assertEquals(resultSet.getElements().get(0).getResults().get(0).getValue(DateTime.class).orNull(),
                 new DateTime(2015, 5, 6, 14, 0, 0, DateTimeZone.UTC));
  }

  @Test
  public void testDeserializationPotentials()
  {
    final String ser = "{\"elements\":[{\"inputs\":[\"Alice\",\"Bob\"],\"name\":\"people\",\"results\":[{\"potentialvalues\":[{\"name\":\"Alice Jones\",\"email\":\"aj@test.com\"},{\"name\":\"Alice Smith\",\"email\":\"as@test.com\"}],\"state\":\"Ambiguous\"},{\"potentialvalues\":[{\"name\":\"Bob Jones\",\"email\":\"bj@test.com\"},{\"name\":\"Bob Smith\",\"email\":\"bs@test.com\"}],\"state\":\"Ambiguous\"}]}],\"state\":\"Ambiguous\"}";

    final ResultSet resultSet = WObject.deserialize(ser, ResultSet.class);
    assertNotNull(resultSet);
    assertEquals(resultSet.getElements()
                          .get(0)
                          .getResults()
                          .get(0)
                          .getPotentialValues(new TypeReference<ImmutableList<Person>>() {})
                          .get()
                          .get(0), new Person("Alice Jones", "aj@test.com"));
  }
}
