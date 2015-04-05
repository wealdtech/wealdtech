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
 * A token-based authentication method
 */
public class TokenAuthenticationMethod extends AbstractAuthenticationMethod
{
  private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationMethod.class);

  public static final String TOKEN_AUTHENTICATION = "Token";

  private static final String TOKEN = "token";

  @JsonCreator
  public TokenAuthenticationMethod(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TOKEN), "Token authentication method failed validation: must contain token");
  }

  @JsonIgnore
  public String getType(){return TOKEN_AUTHENTICATION;}

  @JsonIgnore
  public String getToken(){return get(TOKEN, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends AbstractAuthenticationMethod.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, TOKEN_AUTHENTICATION);
    }

    public Builder(final TokenAuthenticationMethod prior)
    {
      super(prior);
      data(TYPE, TOKEN_AUTHENTICATION);
    }

    public P token(final String token)
    {
      data(TOKEN, token);
      return self();
    }

    public TokenAuthenticationMethod build(){ return new TokenAuthenticationMethod(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final TokenAuthenticationMethod prior)
  {
    return new Builder(prior);
  }
}
