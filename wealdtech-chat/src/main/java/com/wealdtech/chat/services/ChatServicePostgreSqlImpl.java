/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;
import com.wealdtech.WealdError;
import com.wealdtech.chat.Chat;
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

/**
 * Chat service using PostgreSQL as a backend
 */
public class ChatServicePostgreSqlImpl implements ChatService
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatServicePostgreSqlImpl.class);

  private final PostgreSqlRepository repository;


  private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS t_chat(f_data JSON NOT NULL)";

  private static final String DESTROY_SQL = "DROP TABLE t_chat";

  private static final String ADD_SQL = "INSERT INTO t_chat VALUES(?)";

  private static final String GET_CHATS_SQL = "SELECT f_data\n" +
                                              "FROM t_chat\n" +
                                              "WHERE f_data->>'from' = ?";
  @Inject
  public ChatServicePostgreSqlImpl(final PostgreSqlRepository repository)
  {
    this.repository = repository;
  }

  @Override
  public void createDatastore()
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(CREATE_SQL);
      stmt.execute();
    }
    catch (final SQLException se)
    {
      handleSqlFailure(conn, se, "Failed to create chat service datastore");
    }
  }

  @Override
  public void destroyDatastore()
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(DESTROY_SQL);
      stmt.execute();
    }
    catch (final SQLException se)
    {
      handleSqlFailure(conn, se, "Failed to destroy chat service datastore");
    }
  }

  @Override
  public void addChat(final Chat chat)
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(ADD_SQL);
      final PGobject obj = new PGobject();
      obj.setType("json");
      try
      {
        obj.setValue(WealdMapper.getServerMapper().writeValueAsString(chat));
      }
      catch (final JsonProcessingException jpe)
      {
        throw new ServerError("Failed to create json for insertion in to database", jpe);
      }
      stmt.setObject(1, obj);

      stmt.execute();
    }
    catch (final SQLException se)
    {
      throw handleSqlFailure(conn, se, "Failed to add chat to chat service datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  @Override
  public ImmutableList<Chat> getChats(final String from, @Nullable final String topic)
  {
    Connection conn = null;
    try
    {
      conn = repository.getConnection();

      final PreparedStatement stmt = conn.prepareStatement(GET_CHATS_SQL);
      stmt.setString(1, from);

      try (ResultSet rs = stmt.executeQuery())
      {
        final ImmutableList.Builder<Chat> chatsB = ImmutableList.builder();
        while (rs.next())
        {
          try
          {
            chatsB.add(WealdMapper.getServerMapper().readValue(rs.getString(1), Chat.class));
          }
          catch (final IOException ioe)
          {
            LOG.error("Failed to parse chat: ", ioe);
            throw new ServerError("Failed to obtain information");
          }
        }
        return chatsB.build();
      }
    }
    catch (final SQLException se)
    {
     throw handleSqlFailure(conn, se, "Failed to add chat to chat service datastore");
    }
    finally
    {
      closeConnection(conn);
    }
  }

  /**
   * Handle a SQL failure, parsing the output and logging relevant information.  Throw an exception when done.
   * @param se A database connection.
   * @param se The SQL exception.
   * @param throwMessage The message to pass back when throwing an exception.
   * @throws com.wealdtech.DataError Thrown when the error is due to bad data.
   * @throws ServerError Thrown when the error is due to a server issue.
   */
  public static WealdError handleSqlFailure(final Connection conn, final SQLException se, final String throwMessage)
  {
    return handleSqlFailure(conn, se, throwMessage, null);
  }

  /**
   * Handle a SQL failure, parsing the output and logging relevant information.  Throw an exception when done.
   * @param conn A database connection.
   * @param se The SQL exception.
   * @param throwMessage The message to pass back when throwing an exception.
   * @param obj the object which caused the problem
   * @throws com.wealdtech.DataError Thrown when the error is due to bad data.
   * @throws ServerError Thrown when the error is due to a server issue.
   */
  public static WealdError handleSqlFailure(final Connection conn, final SQLException se, final String throwMessage, final Object obj)
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
        LOG.warn("Failed to rollback database connection on \"" + throwMessage + "\"");
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
        LOG.warn("Failed to close database connection");
      }
    }
  }
}
