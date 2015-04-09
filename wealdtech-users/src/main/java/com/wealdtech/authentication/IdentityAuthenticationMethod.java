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
import com.fasterxml.jackson.core.type.TypeReference;
import com.wealdtech.User;
import com.wealdtech.WID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * An authentication method where a user can identify themself as another user
 */
public class IdentityAuthenticationMethod extends AuthenticationMethod
{
  private static final Logger LOG = LoggerFactory.getLogger(IdentityAuthenticationMethod.class);

  public static final String IDENTITY_AUTHENTICATION = "Identity";

  private static final String USER_ID = "userid";

  @JsonCreator
  public IdentityAuthenticationMethod(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(USER_ID), "Identity authentication method failed validation: must contain user ID");
  }

  @JsonIgnore
  public String getType(){return IDENTITY_AUTHENTICATION;}

  @JsonIgnore
  private static final TypeReference<WID<User>> USER_ID_TYPEREF = new TypeReference<WID<User>>(){};

  @JsonIgnore
  public WID<User> getUserId(){return get(USER_ID, USER_ID_TYPEREF).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends AuthenticationMethod.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, IDENTITY_AUTHENTICATION);
    }

    public Builder(final IdentityAuthenticationMethod prior)
    {
      super(prior);
      data(TYPE, IDENTITY_AUTHENTICATION);
    }

    public P userId(final WID<User> userId)
    {
      data(USER_ID, userId);
      return self();
    }

    public IdentityAuthenticationMethod build(){ return new IdentityAuthenticationMethod(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final IdentityAuthenticationMethod prior)
  {
    return new Builder(prior);
  }
}
