/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class OAuth2Credentials extends WObject<OAuth2Credentials> implements Comparable<OAuth2Credentials>
{
  private static final String ACCESS_TOKEN = "accesstoken";
  private static final String REFRESH_TOKEN = "refreshtoken";
  private static final String EXPIRES = "expires";

  private static final Logger LOG = LoggerFactory.getLogger(OAuth2Credentials.class);

  @JsonIgnore
  public Optional<String> getAccessToken() { return get(ACCESS_TOKEN, String.class); }

  @JsonIgnore
  public Optional<String> getRefreshToken() { return get(REFRESH_TOKEN, String.class); }

  @JsonIgnore
  public Optional<DateTime> getExpires() { return get(EXPIRES, DateTime.class); }

  @JsonCreator
  public OAuth2Credentials(final Map<String, Object> data)
  {
    super(data);
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<OAuth2Credentials, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final OAuth2Credentials prior)
    {
      super(prior);
    }

    public P accessToken(final String accessToken)
    {
      data(ACCESS_TOKEN, accessToken);
      return self();
    }

    public P refreshToken(final String refreshToken)
    {
      data(REFRESH_TOKEN, refreshToken);
      return self();
    }

    public P expires(final DateTime expires)
    {
      data(EXPIRES, expires);
      return self();
    }

    public OAuth2Credentials build()
    {
      return new OAuth2Credentials(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final OAuth2Credentials prior)
  {
    return new Builder(prior);
  }

}
