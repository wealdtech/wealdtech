/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.repositories.repository;

import com.google.inject.Inject;
import com.wealdtech.repositories.DatastoreConnection;
import com.wealdtech.repositories.config.PostgreSqlConfiguration;

import java.sql.Connection;

/**
 */
public class PostgreSqlRepository implements Repository<Connection>
{
  private final PostgreSqlConfiguration configuration;
  private final String url;

  @Inject
  public PostgreSqlRepository(final PostgreSqlConfiguration configuration)
  {
    this.configuration = configuration;
    final StringBuilder sb = new StringBuilder(250);
    sb.append("jdbc:postgresql://");
    sb.append(this.configuration.getHost());
    sb.append(':');
    sb.append(this.configuration.getPort());
    sb.append('/');
    sb.append(this.configuration.getName());
    if (this.configuration.getAdditionalParams().isPresent())
    {
      sb.append('?');
      sb.append(this.configuration.getAdditionalParams());
    }
    this.url = sb.toString();
  }

  @Override
  public Connection getConnection()
  {
    return DatastoreConnection.getConnection(this.url, this.configuration.getUsername(), this.configuration.getPassword(),
                                             this.configuration.getConnectionAttempts(),
                                             this.configuration.getConnectionAttemptGap());
  }
}
