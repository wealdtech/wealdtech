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
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.Trace;
import com.wealdtech.WID;
import com.wealdtech.activities.Activity;
import com.wealdtech.contexts.Context;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TraceServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Trace> implements TraceService<PreparedStatement>
{
  private static final Logger LOG = LoggerFactory.getLogger(TraceServicePostgreSqlImpl.class);

  private static final TypeReference<Trace> TRACE_TYPE_REFERENCE = new TypeReference<Trace>() {};

  @Inject
  public TraceServicePostgreSqlImpl(final PostgreSqlRepository repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "trace");
  }

  @Override
  public void createDatastore()
  {
    super.createDatastore();
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt1 = conn.prepareStatement("ALTER TABLE t_trace ADD COLUMN atsv tsvector");
      stmt1.execute();
      final PreparedStatement stmt2 = conn.prepareStatement("ALTER TABLE t_trace ADD COLUMN ctsv tsvector");
      stmt2.execute();
      final PreparedStatement stmt3 = conn.prepareStatement("CREATE INDEX i_trace_atsv ON t_trace USING gin(atsv)");
      stmt3.execute();
      final PreparedStatement stmt4 = conn.prepareStatement("CREATE INDEX i_trace_ctsv ON t_trace USING gin(ctsv)");
      stmt4.execute();
      final PreparedStatement stmt5 = conn.prepareStatement(
          "CREATE FUNCTION t_trace_trigger1() RETURNS trigger AS $$\n" + "BEGIN\n" +
          "  new.atsv := to_tsvector('english', (SELECT string_agg(item.value, ' ')\n" +
          "                                      FROM jsonb_each_text(new.d->'activity') item));\n" +
          "  new.ctsv := to_tsvector('english', (SELECT string_agg(item.value, ' ')\n" +
          "                                      FROM jsonb_array_elements(new.d->'contexts') element," +
          "                                           jsonb_each_text(element) item));\n" +
          "  return new;\n" + "END\n" +
          "$$ LANGUAGE plpgsql;");
      stmt5.execute();
      final PreparedStatement stmt6 = conn.prepareStatement(
          "CREATE TRIGGER t_trace_tsv BEFORE INSERT OR UPDATE\n" + "ON t_trace FOR EACH ROW EXECUTE PROCEDURE t_trace_trigger1()");
      stmt6.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to create datastore");
    }
  }

  @Override
  public void destroyDatastore()
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt1 = conn.prepareStatement("DROP TRIGGER t_trace_tsv ON t_trace");
      stmt1.execute();
      final PreparedStatement stmt2 = conn.prepareStatement("DROP FUNCTION t_trace_trigger1()");
      stmt2.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to destroy datastore");
    }
    super.destroyDatastore();
  }

  @Override
  public Trace obtain(final WID<Trace> traceId)
  {
    return obtain(Trace.class, traceId);
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
  public ImmutableList<Trace> obtain(final ImmutableSet<Context> contexts,
                                     final ImmutableSet<Activity> activities,
                                     final Range<LocalDateTime> timeframe)
  {
    return query(TRACE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getQuery()
      {
        final boolean hasActivities = activities != null && !activities.isEmpty();
        final boolean hasContexts = contexts != null && !contexts.isEmpty();

        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT d FROM (SELECT d->>'_id' AS id");
        if (hasActivities)
        {
          sb.append(", arank");
        }
        if (hasContexts)
        {
          sb.append(", crank");
        }
        sb.append(" FROM t_trace");
        if (hasActivities)
        {
          sb.append(",to_tsquery(?) AS aq, ts_rank_cd(atsv, aq) AS arank");
        }
        if (hasContexts)
        {
          sb.append(",to_tsquery(?) AS cq, ts_rank_cd(ctsv, cq) AS crank");
        }
        sb.append(" WHERE ");
        if (hasActivities && hasContexts)
        {
          sb.append("(");
        }
        if (hasActivities)
        {
          sb.append("atsv @@ aq");
        }
        if (hasActivities && hasContexts)
        {
          sb.append(" OR ");
        }
        if (hasContexts)
        {
          sb.append("ctsv @@ cq");
        }
        if (hasActivities && hasContexts)
        {
          sb.append(")");
        }
        if (hasActivities || hasContexts)
        {
          sb.append(" AND ");
          if (hasActivities)
          {
            if (hasContexts)
            {
              sb.append("arank > 0 AND crank > 0");
            }
            else
            {
              sb.append("arank > 0");
            }
          }
          else
          {
            sb.append("crank > 0");
          }
          sb.append(" AND ");
        }
        sb.append("(d->>'timestamp')::BIGINT < ? AND (d->>'timestamp')::BIGINT > ?");
        sb.append(" ORDER BY ");
        if (hasActivities)
        {
          sb.append("arank");
        }
        if (hasActivities && hasContexts)
        {
          sb.append(" + ");
        }
        if (hasContexts)
        {
          sb.append("crank");
        }
        if (hasActivities || hasContexts)
        {
          sb.append(" DESC, ");
        }
        sb.append("(d->>'timestamp')::BIGINT");

        sb.append(") AS ids INNER JOIN t_trace d on d->>'_id' = ids.id");
        return sb.toString();
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;

        if (activities != null && !activities.isEmpty())
        {
          final List<String> activitiesValues = Lists.newArrayList();
          for (final Activity activity : activities)
          {
            final List<String> activityValues = Lists.newArrayList();
            String type = null;
            for (final Map.Entry<String, Object> entry : ((Map<String, Object>)activity.getData()).entrySet())
            {
              if (Objects.equal(entry.getKey(), "type"))
              {
                type = entry.getValue().toString();
              }
              else
              {
                activityValues.add(entry.getValue().toString());
              }
            }
            activitiesValues.add("(" + type + " & (" + Joiner.on(" | ").join(activityValues) + "))");
          }
          setString(stmt, index++, Joiner.on(" & ").join(activitiesValues));
        }
        if (contexts != null && !contexts.isEmpty())
        {
          final List<String> contextsValues = Lists.newArrayList();
          for (final Context context : contexts)
          {
            final List<String> contextValues = Lists.newArrayList();
            String type = null;
            for (final Map.Entry<String, Object> entry : ((Map<String, Object>)context.getData()).entrySet())
            {
              if (Objects.equal(entry.getKey(), "type")) {
                type = entry.getValue().toString(); }
                else
              {
                contextValues.add(entry.getValue().toString());
              }
            }
            contextsValues.add("(" + type + " & (" + Joiner.on(" | ").join(contextValues) + "))");
          }
          setString(stmt, index++, Joiner.on(" & ").join(contextsValues));
        }
        setLong(stmt, index++, timeframe.upperEndpoint().toDateTime(DateTimeZone.UTC).getMillis());
        setLong(stmt, index++, timeframe.lowerEndpoint().toDateTime(DateTimeZone.UTC).getMillis());
      }
    });
  }

  @Override
  public void remove(final Trace trace)
  {
    if (trace != null) { remove(trace.getId()); }
  }
}
