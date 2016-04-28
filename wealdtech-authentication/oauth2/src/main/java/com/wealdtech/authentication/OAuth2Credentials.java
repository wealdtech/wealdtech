/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * OAuth2-based credentials for authentication.
 * OAuth2-based credentials contain a name, an access token, the date that the access token expires, and a refresh token
 */
public class OAuth2Credentials extends Credentials
{
  public static final String OAUTH2_CREDENTIALS = "OAuth2";
  private static final Logger LOG = LoggerFactory.getLogger(OAuth2Credentials.class);
  private static final String NAME = "name";
  private static final String ACCESS_TOKEN = "accesstoken";
  private static final String SCOPES = "scopes";
  private static final String EXPIRES = "expires";
  private static final String REFRESH_TOKEN = "refreshtoken";

  private static final TypeReference<ImmutableSet<String>> SCOPES_TYPE_REF = new TypeReference<ImmutableSet<String>>() {};

  @JsonCreator
  public OAuth2Credentials(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "OAuth2 authentication method failed validation: must contain name");
    checkState(exists(ACCESS_TOKEN), "OAuth2 authentication method failed validation: must contain access token");
    checkState(exists(SCOPES), "OAuth2 authentication method failed validation: must contain scopes");
    checkState(exists(EXPIRES), "OAuth2 authentication method failed validation: must contain expires");
    checkState(exists(REFRESH_TOKEN), "OAuth2 authentication method failed validation: must contain refresh token");
  }

  @JsonIgnore
  public String getType(){return OAUTH2_CREDENTIALS;}

  @JsonIgnore
  public String getName(){return get(NAME, String.class).get();}

  @JsonIgnore
  public ImmutableSet<String> getScopes(){return get(SCOPES, SCOPES_TYPE_REF).get();}

  @JsonIgnore
  public String getAccessToken(){return get(ACCESS_TOKEN, String.class).get();}

  @JsonIgnore
  public DateTime getExpires(){return get(EXPIRES, DateTime.class).get();}

  @JsonIgnore
  public String getRefreshToken(){return get(REFRESH_TOKEN, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends Credentials.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, OAUTH2_CREDENTIALS);
    }

    public Builder(final OAuth2Credentials prior)
    {
      super(prior);
      data(TYPE, OAUTH2_CREDENTIALS);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P scopes(final ImmutableSet<String> scopes)
    {
      data(SCOPES, scopes);
      return self();
    }

    public P accessToken(final String accessToken)
    {
      data(ACCESS_TOKEN, accessToken);
      return self();
    }

    public P expires(final DateTime expires)
    {
      data(EXPIRES, expires);
      return self();
    }

    public P refreshToken(final String refreshToken)
    {
      data(REFRESH_TOKEN, refreshToken);
      return self();
    }

    public OAuth2Credentials build(){ return new OAuth2Credentials(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final OAuth2Credentials prior)
  {
    return new Builder(prior);
  }
}
