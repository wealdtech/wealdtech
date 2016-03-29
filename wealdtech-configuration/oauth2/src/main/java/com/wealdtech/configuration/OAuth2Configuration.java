/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generic configuration for an OAuth2 setup
 */
public class OAuth2Configuration implements Configuration
{
  private final String clientId;
  private final String secret;
  private final URL callbackUrl;

  @JsonCreator
  public OAuth2Configuration(@JsonProperty("clientid") final String clientId,
                             @JsonProperty("secret") final String secret,
                             @JsonProperty("callbackurl") final URL callbackUrl)
  {
    this.clientId = clientId;
    this.secret = secret;
    this.callbackUrl = callbackUrl;
  }

  public String getClientId() { return clientId; }

  public String getSecret() { return secret; }

  public URL getCallbackUrl() { return callbackUrl; }

  public URL generateAuthorizationUrl() throws MalformedURLException
  {
    return new URL("https://accounts.google.com/o/oauth2/auth?" +
                   "client_id=" + getClientId() +
                   "&response_type=code" +
                   "&scope=openid%20profile%20email%20https://www.googleapis.com/auth/calendar" +
                   "&redirect_uri=" + getCallbackUrl() +
                   "&state=auth" +
                   "&access_type=offline");
  }
}
