/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.ServerError;
import com.wealdtech.configuration.Configuration;

/**
 */
public class PushWooshConfiguration implements Configuration
{
  private final String appId;
  private final String apiKey;

  public PushWooshConfiguration()
  {
    throw new ServerError("Not allowed to create PushWoosh services configuration without information");
  }

  @JsonCreator
  public PushWooshConfiguration(@JsonProperty("appid") final String appId,
                                @JsonProperty("apikey") final String apiKey)
  {
    this.appId = appId;
    this.apiKey = apiKey;
  }

  public String getAppId()
  {
    return this.appId;
  }

  public String getApiKey()
  {
    return this.apiKey;
  }

}
