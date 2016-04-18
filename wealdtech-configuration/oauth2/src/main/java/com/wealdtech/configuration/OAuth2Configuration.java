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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.net.URI;

/**
 * Generic configuration for an OAuth2 setup
 */
public class OAuth2Configuration implements Configuration
{
  private final String clientId;
  private final String secret;
  private final URI callbackUri;

  @JsonCreator
  public OAuth2Configuration(@JsonProperty("clientid") final String clientId,
                             @JsonProperty("secret") final String secret,
                             @JsonProperty("callbackuri") final URI callbackUri)
  {
    this.clientId = clientId;
    this.secret = secret;
    this.callbackUri = callbackUri;
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static OAuth2Configuration fromEnv(final String base)
  {
    final URI callbackUri = System.getenv(base + "_callbackuri") == null ? null : URI.create(System.getenv(base + "_callbackuri"));
    return new OAuth2Configuration(System.getenv(base + "_clientid"), System.getenv(base + "_secret"), callbackUri);
  }

  public String getClientId() { return clientId; }

  public String getSecret() { return secret; }

  public URI getCallbackUri() { return callbackUri; }

  public URI generateAuthorizationUri(ImmutableList<String> scopes)
  {
    return URI.create("https://accounts.google.com/o/oauth2/auth?" +
                      "client_id=" + getClientId() +
                      "&response_type=code" +
                      "&scope=" + Joiner.on("%20").join(scopes) +
                      "&redirect_uri=" + getCallbackUri() +
                      "&state=auth" +
                      "&access_type=offline");
  }
}
