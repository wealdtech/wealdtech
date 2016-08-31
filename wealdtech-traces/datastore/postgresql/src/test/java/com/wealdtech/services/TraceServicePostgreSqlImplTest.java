/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.wealdtech.Trace;
import com.wealdtech.WID;
import com.wealdtech.activities.Activity;
import com.wealdtech.activities.GenericActivity;
import com.wealdtech.activities.MealActivity;
import com.wealdtech.contexts.Context;
import com.wealdtech.contexts.LocationContext;
import com.wealdtech.contexts.NamedEntityContext;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.joda.time.LocalDateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class TraceServicePostgreSqlImplTest
{
  private TraceServicePostgreSqlImpl traceService;

  private Trace mealTrace1, mealTrace2, meetingTrace1;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null));

    traceService = new TraceServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                         .copy()
                                                                         .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    traceService.createDatastore();

    {
      final MealActivity dinnerActivity = MealActivity.builder().mealType(MealActivity.MealType.DINNER).build();
      final NamedEntityContext mikeContext =
          NamedEntityContext.builder().name("Mike").gender(NamedEntityContext.Gender.MALE).build();
      final LocationContext restaurantContext =
          LocationContext.builder().name("Eleven Madison Park").locationType(LocationContext.Type.RESTAURANT).build();
      mealTrace1 = Trace.builder()
                        .id(WID.<Trace>generate())
                        .contexts(ImmutableSet.<Context>of(mikeContext, restaurantContext))
                        .activities(ImmutableSet.<Activity>of(dinnerActivity))
                        .timestamp(new LocalDateTime(2020, 1, 1, 19, 0, 0))
                        .build();
      traceService.add(mealTrace1);
    }

    {
      final MealActivity dinnerActivity = MealActivity.builder().mealType(MealActivity.MealType.DINNER).build();
      final NamedEntityContext janeContext =
          NamedEntityContext.builder().name("Jane").gender(NamedEntityContext.Gender.FEMALE).build();
      final LocationContext restaurantContext =
          LocationContext.builder().name("Eleven Madison Park").locationType(LocationContext.Type.RESTAURANT).build();
      mealTrace2 = Trace.builder()
                        .id(WID.<Trace>generate())
                        .contexts(ImmutableSet.<Context>of(janeContext, restaurantContext))
                        .activities(ImmutableSet.<Activity>of(dinnerActivity))
                        .timestamp(new LocalDateTime(2020, 1, 2, 19, 0, 0))
                        .build();
      traceService.add(mealTrace2);
    }

    {
      final GenericActivity meetingActivity = GenericActivity.builder().data("meetingtype", "In person").build();
      final NamedEntityContext mikeContext =
          NamedEntityContext.builder().name("Mike").gender(NamedEntityContext.Gender.MALE).build();
      final LocationContext officeContext =
          LocationContext.builder().name("Cognis HQ").locationType(LocationContext.Type.WORKPLACE).build();
      meetingTrace1 = Trace.builder()
                           .id(WID.<Trace>generate())
                           .contexts(ImmutableSet.<Context>of(mikeContext, officeContext))
                           .activities(ImmutableSet.<Activity>of(meetingActivity))
                           .timestamp(new LocalDateTime(2020, 1, 2, 21, 0, 0))
                           .build();
      traceService.add(meetingTrace1);
    }
  }

  @AfterClass
  public void tearDown()
  {
    if (traceService != null)
    {
      traceService.destroyDatastore();
    }
  }

  @Test
  public void testObtainById()
  {
    final Trace dbTrace = traceService.obtain(mealTrace1.getId());
    assertEquals(dbTrace, mealTrace1);
  }

  @Test
  public void testObtainByRange()
  {
    final ImmutableList<Trace> dbTraces =
        traceService.obtain(Range.closedOpen(new LocalDateTime(2020, 1, 2, 0, 0, 0), new LocalDateTime(2020, 1, 3, 0, 0, 0)));
    assertEquals(dbTraces.size(), 2);
    assertEquals(dbTraces.get(0), mealTrace2);
    assertEquals(dbTraces.get(1), meetingTrace1);
  }

  @Test
  public void testObtainByContext()
  {
    final NamedEntityContext context = NamedEntityContext.builder().name("Mike").gender(NamedEntityContext.Gender.MALE).build();
    final ImmutableList<Trace> dbTraces = traceService.obtain(ImmutableSet.<Context>of(context), ImmutableSet.<Activity>of(),
                                                              Range.closedOpen(new LocalDateTime(2020, 1, 1, 0, 0, 0),
                                                                               new LocalDateTime(2020, 1, 14, 0, 0, 0)));
    assertEquals(dbTraces.size(), 2);
    assertEquals(dbTraces.get(0), mealTrace1);
    assertEquals(dbTraces.get(1), meetingTrace1);
  }

  @Test
  public void testObtainByActivity()
  {
    final MealActivity activity = MealActivity.builder().mealType(MealActivity.MealType.DINNER).build();
    final ImmutableList<Trace> dbTraces = traceService.obtain(ImmutableSet.<Context>of(), ImmutableSet.<Activity>of(activity),
                                                              Range.closedOpen(new LocalDateTime(2020, 1, 1, 0, 0, 0),
                                                                               new LocalDateTime(2020, 1, 14, 0, 0, 0)));
    assertEquals(dbTraces.size(), 2);
    assertEquals(dbTraces.get(0), mealTrace1);
    assertEquals(dbTraces.get(1), mealTrace2);
  }

  @Test
  public void testObtainByContextAndActivity()
  {
    final NamedEntityContext context = NamedEntityContext.builder().name("Mike").gender(NamedEntityContext.Gender.MALE).build();
    final MealActivity activity = MealActivity.builder().mealType(MealActivity.MealType.DINNER).build();
    final ImmutableList<Trace> dbTraces = traceService.obtain(ImmutableSet.<Context>of(context), ImmutableSet.<Activity>of(activity),
                                                              Range.closedOpen(new LocalDateTime(2020, 1, 1, 0, 0, 0),
                                                                               new LocalDateTime(2020, 1, 14, 0, 0, 0)));
    assertEquals(dbTraces.size(), 1);
    assertEquals(dbTraces.get(0), mealTrace1);
  }
}
