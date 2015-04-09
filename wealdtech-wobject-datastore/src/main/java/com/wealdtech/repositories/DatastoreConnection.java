/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.repositories;

import com.wealdtech.ServerError;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.postgresql.PGConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public enum DatastoreConnection
{
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(DatastoreConnection.class);

  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  public static Connection getConnection(final String url, final String username, final String password,
                                         final int connectionAttempts, final long connectionAttemptGap)
  {
    int retryCount = connectionAttempts;
    while (retryCount-- > 0)
    {
      try
      {
        DataSource datasource = dataSources.get(url);
        if (datasource == null)
        {
          datasource = DatastoreConnection.initDataSource(url, username, password);
        }
        final Connection conn = datasource.getConnection();
        // PostgreSQL-specific connection parameters
        final PGConnection pgconn = (PGConnection)((javax.sql.PooledConnection)conn).getConnection();
//        pgconn.addDataType("interval", com.wealdtech.utils.db.PGPeriod.class);
//        pgconn.addDataType("datetime", com.wealdtech.utils.db.PGDateTime.class);
//        pgconn.addDataType("localdate", com.wealdtech.utils.db.PGLocalDate.class);
//        pgconn.addDataType("localdatetime", com.wealdtech.utils.db.PGLocalDateTime.class);
//        pgconn.addDataType("jinterval", com.wealdtech.utils.db.PGInterval.class);
        return conn;
      }
      catch (final SQLException se)
      {
        LOG.debug("Failed to connect to database: {}", se.getLocalizedMessage());
        try { Thread.sleep(connectionAttemptGap); } catch (final InterruptedException e) {}
      }
    }
    throw new ServerError("Failed to connection to database");
  }

  private static DataSource initDataSource(final String url, final String username, final String password)
  {
    final PoolProperties props = new PoolProperties();

    props.setUrl(url);
    props.setDriverClassName("org.postgresql.Driver");
    props.setUsername(username);
    props.setPassword(password);

    props.setJmxEnabled(true);
    props.setTestWhileIdle(false);
    props.setTestOnBorrow(true);
    props.setValidationQuery("SELECT 1");
    props.setTestOnReturn(false);
    props.setValidationInterval(30000);
    props.setTimeBetweenEvictionRunsMillis(30000);

    props.setMaxActive(64);
    props.setMaxIdle(32);
    props.setInitialSize(16);
    props.setMaxWait(10000);
    props.setRemoveAbandonedTimeout(60);
    props.setMinEvictableIdleTimeMillis(30000);
    props.setMinIdle(16);
    props.setDefaultAutoCommit(true);

    // This would be useful but has a high performance impact so we need to turn it off.  We can re-enable it if there is a specific
    // issue to track down
    props.setLogAbandoned(false);

    props.setRemoveAbandoned(true);

    props.setInitSQL("SET INTERVALSTYLE TO 'iso_8601'");

    props.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                              "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

    final DataSource datasource = new DataSource();
    datasource.setPoolProperties(props);
    dataSources.put(url, datasource);
    return datasource;
  }

  public static void closeDatasource(final String url)
  {
    final DataSource datasource = dataSources.get(url);
    if (datasource != null)
    {
      datasource.close();
      dataSources.remove(url);
    }
  }
}
