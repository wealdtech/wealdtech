/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.remote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.DataError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class OAuth2Remote extends Remote<OAuth2Remote> implements Comparable<OAuth2Remote>
{
  private static final Logger LOG = LoggerFactory.getLogger(OAuth2Remote.class);

  private static final String ACCESS_TOKEN = "accesstoken";
  private static final String REFRESH_TOKEN = "refreshtoken";

  @JsonCreator
  public OAuth2Remote(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    super.validate();
    if (!exists(ACCESS_TOKEN))
    {
      throw new DataError.Missing("OAuth2Remote needs 'accesstoken' information");
    }
    if (!exists(REFRESH_TOKEN))
    {
      throw new DataError.Missing("OAuth2Remote needs 'refreshtoken' information");
    }
  }

  @JsonIgnore
  public String getAccessToken()
  {
    return get(ACCESS_TOKEN, String.class).get();
  }

  @JsonIgnore
  public String getRefreshToken()
  {
    return get(REFRESH_TOKEN, String.class).get();
  }

  public static class Builder<P extends Builder<P>> extends Remote.Builder<OAuth2Remote, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final OAuth2Remote prior)
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

    public OAuth2Remote build()
    {
      return new OAuth2Remote(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final OAuth2Remote prior)
  {
    return new Builder(prior);
  }
}
