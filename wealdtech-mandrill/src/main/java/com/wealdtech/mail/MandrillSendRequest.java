/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

/**
 * Request to Mandrill server for sending an email
 */
public class MandrillSendRequest
{
  private final String key;
  @JsonProperty("template_name")
  private final String templateName;
  @JsonProperty("template_content")
  private final Map<String, String> templateContent;
  @JsonProperty("message")
  private final MandrillMessage message;
  private final boolean async = false;

  @JsonCreator
  public MandrillSendRequest(@JsonProperty("key") final String key,
                             @JsonProperty("templatename") final String templateName,
                             @JsonProperty("templatecontent") final Map<String, String> templateContent,
                             @JsonProperty("message") final MandrillMessage message)
  {
    this.key = key;
    this.templateName = templateName;
    this.templateContent = templateContent;
    this.message = message;
  }
}
