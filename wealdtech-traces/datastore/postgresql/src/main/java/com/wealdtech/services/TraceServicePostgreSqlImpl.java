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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.Trace;
import com.wealdtech.WID;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 *
 */
public class TraceServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Trace> implements TraceService
{
  private static final Logger LOG = LoggerFactory.getLogger(TraceServicePostgreSqlImpl.class);

  private static final TypeReference<Trace> TRACE_TYPE_REFERENCE = new TypeReference<Trace>() {};

  @Inject
  public TraceServicePostgreSqlImpl(final PostgreSqlRepository repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "trace");
  }

  @Override
  public void create(final Trace trace)
  {
    super.add(trace);
  }

  @Override
  public Trace obtain(final WID<Trace> traceId)
  {
    return Iterables.getFirst(obtain(TRACE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"_id\":\"" + traceId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableList<Trace> obtain(final Range<LocalDateTime> timeframe)
  {
    return obtain(TRACE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "(d->>'timestamp')::BIGINT < ? AND (d->>'timestamp')::BIGINT > ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setLong(stmt, index++, timeframe.upperEndpoint().toDateTime(DateTimeZone.UTC).getMillis());
        setLong(stmt, index++, timeframe.lowerEndpoint().toDateTime(DateTimeZone.UTC).getMillis());
      }

      @Override
      public String getOrder(){return "(d->>'timestamp')::BIGINT";}
    });
  }

  @Override
  public void remove(final Trace trace)
  {
    super.remove(trace.getId());
  }
}
