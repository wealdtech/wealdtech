/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.mail.MailActor;

import java.util.Map;

/**
 * Configuration for an email system
 */
public class MailConfiguration implements Configuration
{
  final String accountId;
  final String applicationId;
  final String secret;
  final ImmutableMap<String, String> templates;
  final MailActor sender;

  @JsonCreator
  public MailConfiguration(@JsonProperty("accountid") final String accountId,
                           @JsonProperty("applicationid") final String applicationId,
                           @JsonProperty("secret") final String secret,
                           @JsonProperty("sender") final MailActor sender,
                           @JsonProperty("templates") final Map<String, String> templates)
  {
    this.accountId = accountId;
    this.applicationId = applicationId;
    this.secret = secret;
    this.templates = templates == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(templates);
    this.sender = sender;
  }

  public Optional<String> getAccountId(){return Optional.fromNullable(accountId);}

  public Optional<String> getApplicationId(){return Optional.fromNullable(applicationId);}

  public Optional<String> getSecret(){return Optional.fromNullable(secret);}

  public ImmutableMap<String, String> getTemplates(){return templates;}

  public Optional<MailActor> getSender(){return Optional.fromNullable(sender);}
}
