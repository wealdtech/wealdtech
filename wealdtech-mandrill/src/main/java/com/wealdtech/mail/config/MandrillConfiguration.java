/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
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
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.mail.MailActor;

/**
 * Configuration for Mandrill
 */
public class MandrillConfiguration implements Configuration
{
  private String key = "";

  private MailActor sender;

  private boolean active = true;

  @Inject
  public MandrillConfiguration(){}

  @JsonCreator
  private MandrillConfiguration(@JsonProperty("key") final String key,
                                @JsonProperty("sender name") final String senderName,
                                @JsonProperty("sender address") final String senderAddress,
                                @JsonProperty("active") final Boolean active)
  {
    this.key = key;
    this.sender = new MailActor(senderName, senderAddress);
    this.active = MoreObjects.firstNonNull(active, true);
  }

  public String getKey()
  {
    return key;
  }

  public MailActor getSender()
  {
    return sender;
  }

  public boolean isActive() { return active; }
}
