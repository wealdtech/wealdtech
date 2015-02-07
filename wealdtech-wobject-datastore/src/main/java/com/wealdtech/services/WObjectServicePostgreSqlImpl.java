/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.*;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
import com.wealdtech.jackson.WealdMapper;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.wealdtech.Preconditions.checkState;

/**
 * WObject service using PostgreSQL as a backend
 */
public class WObjectServicePostgreSqlImpl<T extends WObject<T>> implements WObjectService<T, PreparedStatement>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObjectServicePostgreSqlImpl.class);

  private final PostgreSqlRepository repository;

  public static transient final char JDBC_VARIABLE = '?';

  private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS t_TABLENAME(d JSONB NOT NULL)";

  private static final String DESTROY_TABLE_SQL = "DROP TABLE IF EXISTS t_TABLENAME";

  private static final String ADD_SQL = "INSERT INTO t_TABLENAME VALUES(?)";

  private static final String REMOVE_SQL = "DELETE FROM t_TABLENAME\n" +
                                           "WHERE d->>'_id' = ?";

  private static final String OBTAIN_SQL = "SELECT d\n" +
                                           "FROM t_TABLENAME";

  private static final String UPDATE_SQL = "UPDATE t_TABLENAME\n" +
                                           "SET d = ?\n" +
                                           "WHERE d->>'_id' = ?";
  private final String createTableSql;
  private final String destroyTableSql;
  private final String addSql;
  private final String removeSql;
  private final String obtainSql;
  private final String updateSql;

  @Inject
  public WObjectServicePostgreSqlImpl(final PostgreSqlRepository repository,
                                      final String tableName)
  {
    this.repository = repository;
    createTableSql = CREATE_TABLE_SQL.replaceAll("TABLENAME", tableName);
    destroyTableSql = DESTROY_TABLE_SQL.replaceAll("TABLENAME", tableName);
    addSql = ADD_SQL.replaceAll("TABLENAME", tableName);
    removeSql = REMOVE_SQL.replaceAll("TABLENAME", tableName);
    obtainSql = OBTAIN_SQL.replaceAll("TABLENAME", tableName);
    updateSql = UPDATE_SQL.replaceAll("TABLENAME", tableName);
  }

  @Override
  public void createDatastore()
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(createTableSql);
      stmt.execute();
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

      final PreparedStatement stmt = conn.prepareStatement(destroyTableSql);
      stmt.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to destroy datastore");
    }
  }

  @Override
  public void add(final T item)
  {
    checkState(item != null, "Passed NULL item for creation in datastore");
    checkState(item.getId() != null, "Passed item with NULL ID for creation in datastore");

    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(addSql);
      final PGobject obj = new PGobject();
      obj.setType("jsonb");
      try
      {
        obj.setValue(WealdMapper.getServerMapper().writeValueAsString(item));
      }
      catch (final JsonProcessingException jpe)
      {
        throw new ServerError("Failed to create json for addition in to datastore", jpe);
      }
      stmt.setObject(1, obj);

      stmt.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to add item to datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  @Override
  public void remove(final WID<T> itemId)
  {
    checkState(itemId != null, "Passed NULL item ID for removal from datastore");

    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(removeSql);
      stmt.setString(1, itemId.toString());
      stmt.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to remove item from datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  @Override
  public void update(final T item)
  {
    checkState(item != null, "Passed NULL item for update in datastore");
    checkState(item.getId() != null, "Passed item with NULL ID for update in datastore");

    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(updateSql);
      final PGobject obj = new PGobject();
      obj.setType("jsonb");
      try
      {
        obj.setValue(WealdMapper.getServerMapper().writeValueAsString(item));
      }
      catch (final JsonProcessingException jpe)
      {
        throw new ServerError("Failed to create json for update in datastore", jpe);
      }
      stmt.setObject(1, obj);
      stmt.setString(2, item.getId().toString());
      stmt.execute();
    }
    catch (final SQLException se)
    {
      throw createSqlException(conn, se, "Failed to remove item from datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  @Override
  public ImmutableList<T> obtain(final TypeReference<T>typeRef, @Nullable final WObjectServiceCallback<PreparedStatement> cb)
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt;
      if (cb == null || cb.getConditions() == null)
      {
        stmt = conn.prepareStatement(obtainSql);
      }
      else
      {
        final String statement = obtainSql + "\nWHERE " + cb.getConditions();
        stmt = conn.prepareStatement(statement);
        cb.setConditionValues(stmt);
      }

      try (ResultSet rs = stmt.executeQuery())
      {
        final ImmutableList.Builder<T> objsB = ImmutableList.builder();
        while (rs.next())
        {
          try
          {
            objsB.add((T)WealdMapper.getServerMapper().readValue(rs.getString(1), typeRef));
          }
          catch (final IOException ioe)
          {
            LOG.error("Failed to parse object: ", ioe);
            throw new ServerError("Failed to obtain information");
          }
        }
        return objsB.build();
      }
    }
    catch (final SQLException se)
    {
     throw createSqlException(conn, se, "Failed to obtain object from datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  /**
   * Handle a SQL failure, parsing the output and logging relevant information.  Throw an exception when done.
   * @param stmt A prepared statement.
   * @param se The SQL exception.
   * @param throwMessage The message to pass back when throwing an exception.
   * @throws com.wealdtech.DataError Thrown when the error is due to bad data.
   * @throws ServerError Thrown when the error is due to a server issue.
   */
  public static WealdError createSqlException(final PreparedStatement stmt, final SQLException se, final String throwMessage)
  {
    final Connection conn;
    try
    {
      conn = stmt.getConnection();
      return createSqlException(conn, se, throwMessage, null);
    }
    catch (final SQLException ignored)
    {
      return new ServerError("Failed to obtain datastore connection in error!");
    }
  }

  /**
   * Handle a SQL failure, parsing the output and logging relevant information.  Throw an exception when done.
   * @param conn A datastore connection.
   * @param se The SQL exception.
   * @param throwMessage The message to pass back when throwing an exception.
   * @throws com.wealdtech.DataError Thrown when the error is due to bad data.
   * @throws ServerError Thrown when the error is due to a server issue.
   */
  public static WealdError createSqlException(final Connection conn, final SQLException se, final String throwMessage)
  {
    return createSqlException(conn, se, throwMessage, null);
  }

  /**
   * Handle a SQL failure, parsing the output and logging relevant information.  Throw an exception when done.
   * @param conn A datastore connection.
   * @param se The SQL exception.
   * @param throwMessage The message to pass back when throwing an exception.
   * @param obj the object which caused the problem
   * @throws com.wealdtech.DataError Thrown when the error is due to bad data.
   * @throws ServerError Thrown when the error is due to a server issue.
   */
  public static WealdError createSqlException(final Connection conn,
                                              final SQLException se,
                                              final String throwMessage,
                                              final Object obj)
  {
    // First things first, close the connection
    if (conn != null)
    {
      try
      {
        conn.rollback();
      }
      catch (final SQLException se2)
      {
        LOG.warn("Failed to rollback datastore connection on \"" + throwMessage + "\"");
      }
    }

    LOG.warn("SQL failure: ", se);

    if (obj != null)
    {
      LOG.warn("Object which caused the failure: ", obj);
    }

    // Now handle the SQL error
    if (se.getSQLState().startsWith("22") || se.getSQLState().startsWith("23"))
    {
      // Data-related issue
      return new DataError.Bad(throwMessage, se);
    }
    return new ServerError(throwMessage, se);
  }

  public static void closeConnection(final Connection conn)
  {
    if (conn != null)
    {
      try
      {
        if (!conn.getAutoCommit())
        {
          // It would be nice to know if there was a transaction in process and
          // flag it if so. In the meantime just roll back any current
          // transaction to attempt to highlight the issue.
          conn.rollback();
        }
      }
      catch (final SQLException se)
      {
        LOG.warn("Error rolling back transaction", se);
      }
      try
      {
        conn.close();
      }
      catch (final SQLException se)
      {
        LOG.warn("Failed to close datastore connection");
      }
    }
  }
}
