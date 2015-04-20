/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Token-based credentials for authentication.
 * Token-based credentials are based on a single piece of information: the token itself
 */
public class TokenCredentials extends AbstractCredentials
{
  private static final Logger LOG = LoggerFactory.getLogger(TokenCredentials.class);

  public static final String TOKEN_CREDENTIALS = "Token";

  private static final String TOKEN = "token";

  @JsonCreator
  public TokenCredentials(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TOKEN), "token authentication method failed validation: must contain token");
  }

  @JsonIgnore
  public String getType(){return TOKEN_CREDENTIALS;}

  @JsonIgnore
  public String getToken(){return get(TOKEN, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends AbstractCredentials.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, TOKEN_CREDENTIALS);
    }

    public Builder(final TokenCredentials prior)
    {
      super(prior);
      data(TYPE, TOKEN_CREDENTIALS);
    }

    public P token(final String token)
    {
      data(TOKEN, token);
      return self();
    }

    public TokenCredentials build(){ return new TokenCredentials(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final TokenCredentials prior)
  {
    return new Builder(prior);
  }
}
