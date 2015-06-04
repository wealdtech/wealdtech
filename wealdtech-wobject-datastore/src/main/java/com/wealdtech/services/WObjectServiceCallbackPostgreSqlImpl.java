/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import org.postgresql.util.PGobject;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * A callback for PostgreSQL services
 */
public class WObjectServiceCallbackPostgreSqlImpl implements WObjectServiceCallback<PreparedStatement>
{
  public WObjectServiceCallbackPostgreSqlImpl setString(final PreparedStatement stmt, final int index, @Nullable final String val)
  {
    try
    {
      stmt.setString(index, val);
    }
    catch (final SQLException se)
    {
      throw WObjectServicePostgreSqlImpl.createSqlException(stmt, se, "Failed to set string");
    }
    return this;
  }

  public WObjectServiceCallbackPostgreSqlImpl setStringArray(final PreparedStatement stmt,
                                                             final int index,
                                                             @Nullable final ImmutableCollection<String> val)
  {
    try
    {
      if (val == null)
      {
        stmt.setNull(index, Types.ARRAY);
      }
      else
      {
        stmt.setArray(index, stmt.getConnection().createArrayOf("text", val.toArray()));
      }
    }
    catch (final SQLException se)
    {
      throw WObjectServicePostgreSqlImpl.createSqlException(stmt, se, "Failed to set string array");
    }
    return this;
  }

  public WObjectServiceCallbackPostgreSqlImpl setCIStringArray(final PreparedStatement stmt,
                                                             final int index,
                                                             @Nullable final ImmutableCollection<String> val)
  {
    try
    {
      if (val == null)
      {
        stmt.setNull(index, Types.ARRAY);
      }
      else
      {
        stmt.setArray(index, stmt.getConnection().createArrayOf("citext", val.toArray()));
      }
    }
    catch (final SQLException se)
    {
      throw WObjectServicePostgreSqlImpl.createSqlException(stmt, se, "Failed to set case-insensitive string array");
    }
    return this;
  }

  public WObjectServiceCallbackPostgreSqlImpl setWID(final PreparedStatement stmt, final int index, @Nullable final WID<?> val)
  {
    return setString(stmt, index, val == null ? null : val.toString());
  }

  private static final Function<WID<?>, String> WID_ARRAY_TO_STRING_ARRAY = new Function<WID<?>, String>(){
    @Nullable
    @Override
    public String apply(@Nullable final WID<?> input)
    {
      return input == null ? null : input.toString();
    }
  };

  public WObjectServiceCallbackPostgreSqlImpl setWIDArray(final PreparedStatement stmt,
                                                          final int index,
                                                          @Nullable final ImmutableCollection<? extends WID<?>> val)
  {
    return setStringArray(stmt, index,
                          val == null ? null : ImmutableSet.copyOf(Collections2.transform(val, WID_ARRAY_TO_STRING_ARRAY)));
  }

  public WObjectServiceCallbackPostgreSqlImpl setLong(final PreparedStatement stmt,
                                                      final int index,
                                                      @Nullable final Long val)
  {
    try
    {
      if (val == null)
      {
        stmt.setNull(index, Types.BIGINT);
      }
      else
      {
        stmt.setLong(index, val);
      }
    }
    catch (final SQLException se)
    {
      throw WObjectServicePostgreSqlImpl.createSqlException(stmt, se, "Failed to set long");
    }
    return this;
  }

  public WObjectServiceCallbackPostgreSqlImpl setJson(final PreparedStatement stmt, final int index, @Nullable final String val)
  {
    final PGobject obj = new PGobject();
    obj.setType("jsonb");
    try
    {
      obj.setValue(val);
      stmt.setObject(index, obj);
    }
    catch (final SQLException se)
    {
      throw WObjectServicePostgreSqlImpl.createSqlException(stmt, se, "Failed to set JSON");
    }
    return this;
  }

  @Override
  public String getQuery() { return null;  }

  @Override
  public String getConditions()
  {
    return null;
  }

  @Override
  public void setConditionValues(PreparedStatement stmt) {}

  @Override
  public String getOrder() { return null; }

}
