/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.datastore.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * Configuration for a PostgreSQL data store
 */
public class PostgreSqlConfiguration
{
  private String host = "localhost";
  private int port = 5432;
  private String name = "postgres";
  private String username = "user";
  private String password = "pass";
  private Optional<String> additionalParams = Optional.absent();

  private int connectionAttempts = 20;
  private long connectionAttemptGap = 2000l;

  /**
   * Create a default configuration.
   */
  @Inject
  public PostgreSqlConfiguration()
  {
    // Defaults
  }

  public PostgreSqlConfiguration(@JsonProperty("host") final String host,
                                 @JsonProperty("port") final int port,
                                 @JsonProperty("name") final String name,
                                 @JsonProperty("username") final String username,
                                 @JsonProperty("password") final String password,
                                 @JsonProperty("additionalparams") final String additionalParams,
                                 @JsonProperty("connectionattempts") final Integer connectionAttempts,
                                 @JsonProperty("connectionattemptgap") final Long connectionAttemptGap)
  {
    this.host = MoreObjects.firstNonNull(host, this.host);
    this.port = MoreObjects.firstNonNull(port, this.port);
    this.name = MoreObjects.firstNonNull(name, this.name);
    this.username = MoreObjects.firstNonNull(username, this.username);
    this.password = MoreObjects.firstNonNull(password, this.password);
    this.additionalParams = Optional.fromNullable(additionalParams);
    this.connectionAttempts = MoreObjects.firstNonNull(connectionAttempts, this.connectionAttempts);
    this.connectionAttemptGap = MoreObjects.firstNonNull(connectionAttemptGap, this.connectionAttemptGap);
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static PostgreSqlConfiguration fromEnv(final String base)
  {
    final int port = System.getenv(base + "_port") == null ? 5432 : Integer.parseInt(System.getenv(base + "_port"));
    final Integer connectionAttempts = System.getenv(base + "_connectionattempts") == null ? null : Integer.parseInt(System.getenv(base + "_connectionattempts"));
    final Long connectionAttemptGap = System.getenv(base + "_connectionattemptgap") == null ? null : Long.parseLong(System.getenv(base + "_connectionattemptgap"));
    return new PostgreSqlConfiguration(System.getenv(base + "_host"),
                                       port,
                                       System.getenv(base + "_name"),
                                       System.getenv(base + "_username"),
                                       System.getenv(base + "_password"),
                                       System.getenv(base + "_additionalparams"),
                                       connectionAttempts,
                                       connectionAttemptGap);
  }

  public String getHost()
  {
    return this.host;
  }

  public int getPort()
  {
    return this.port;
  }

  public String getName()
  {
    return this.name;
  }

  public String getUsername()
  {
    return this.username;
  }

  public String getPassword()
  {
    return this.password;
  }

  public Optional<String> getAdditionalParams()
  {
    return this.additionalParams;
  }

  /**
   * Obtain the number of attempts which should be made to connect to the repository
   */
  public int getConnectionAttempts()
  {
    return this.connectionAttempts;
  }

  /**
   * Obtain the gap between successive connection attempts, in ms
   */
  public long getConnectionAttemptGap()
  {
    return this.connectionAttemptGap;
  }
}
