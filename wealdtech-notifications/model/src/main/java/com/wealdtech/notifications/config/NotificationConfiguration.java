/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.notifications.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a notification system
 */
public class NotificationConfiguration implements Configuration
{
  final String accountId;
  final String applicationId;
  final String secret;

  @JsonCreator
  private NotificationConfiguration(@JsonProperty("accountid") final String accountId,
                            @JsonProperty("applicationid") final String applicationId,
                            @JsonProperty("secret") final String secret)
  {
    this.accountId = accountId;
    this.applicationId = applicationId;
    this.secret = secret;
  }

  public Optional<String> getAccountId(){return Optional.fromNullable(accountId);}

  public Optional<String> getApplicationId(){return Optional.fromNullable(applicationId);}

  public Optional<String> getSecret(){return Optional.fromNullable(secret);}

  /**
   * Obtain a configuration from the environment
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static NotificationConfiguration fromEnv(final String base)
  {
    return new NotificationConfiguration(System.getenv(base + "_accountid"),
                                         System.getenv(base + "_applicationid"),
                                         System.getenv(base + "_secret"));
  }
}
