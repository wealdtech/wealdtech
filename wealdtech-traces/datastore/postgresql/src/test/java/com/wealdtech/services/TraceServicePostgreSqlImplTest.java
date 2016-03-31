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
import com.google.common.collect.Range;
import com.wealdtech.Trace;
import com.wealdtech.WID;
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

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null));

    traceService = new TraceServicePostgreSqlImpl(repository, WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    traceService.createDatastore();

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
  public void testCreate()
  {
    final Trace trace1 = Trace.builder()
                              .id(WID.<Trace>generate())
                              .type("meeting")
                              .timestamp(new LocalDateTime(2020, 1, 1, 19, 0, 0))
                              .subject("dinner with mike")
                              .build();

    try
    {
      traceService.create(trace1);

      final Trace dbTrace = traceService.obtain(trace1.getId());
      assertEquals(dbTrace, trace1);
    }

    finally
    {
      try
      {
        traceService.remove(trace1);
      }
      catch (final Exception ignored) {}
    }
  }

  @Test
  public void testObtainByRange()
  {
    final Trace trace1 = Trace.builder()
                              .id(WID.<Trace>generate())
                              .type("meeting")
                              .timestamp(new LocalDateTime(2020, 1, 2, 19, 0, 0))
                              .subject("dinner with mike")
                              .build();

    final Trace trace2 = Trace.builder()
                              .id(WID.<Trace>generate())
                              .type("call")
                              .timestamp(new LocalDateTime(2020, 1, 2, 21, 0, 0))
                              .subject("sales update")
                              .build();

    try
    {
      traceService.create(trace1);
      traceService.create(trace2);

      final ImmutableList<Trace> dbTraces =
          traceService.obtain(Range.closedOpen(new LocalDateTime(2020, 1, 2, 0, 0, 0), new LocalDateTime(2020, 1, 3, 0, 0, 0)));
      assertEquals(dbTraces.size(), 2);
      assertEquals(dbTraces.get(0), trace1);
      assertEquals(dbTraces.get(1), trace2);
    }

    finally
    {
      try
      {
        traceService.remove(trace2);
      }
      catch (final Exception ignored) {}
      try
      {
        traceService.remove(trace1);
      }
      catch (final Exception ignored) {}
    }
  }
}
