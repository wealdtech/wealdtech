/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authorisation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.authentication.AuthorisationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Details of authorisation of a given user with a particular scope
 */
public class UserAuthorisation extends WObject<UserAuthorisation> implements Comparable<UserAuthorisation>
{
  private static final Logger LOG = LoggerFactory.getLogger(UserAuthorisation.class);

  private static final String USER_ID = "userid";
  private static final String SCOPE = "scope";

  @JsonCreator
  public UserAuthorisation(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(USER_ID), "User authorisation failed validation: must contain user ID");
    checkState(exists(SCOPE), "User authorisation failed validation: must contain scope");
  }

  @JsonIgnore
  private static final TypeReference<WID<User>> USER_ID_TYPEREF = new TypeReference<WID<User>>(){};

  @JsonIgnore
  public WID<User> getUserId() { return get(USER_ID, USER_ID_TYPEREF).get(); }

  @JsonIgnore
  public AuthorisationScope getScope() { return get(SCOPE, AuthorisationScope.class).get(); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<UserAuthorisation, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final UserAuthorisation prior)
    {
      super(prior);
    }

    public P userId(final WID<User> userId)
    {
      data(USER_ID, userId);
      return self();
    }

    public P scope(final AuthorisationScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public UserAuthorisation build(){ return new UserAuthorisation(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final UserAuthorisation prior)
  {
    return new Builder(prior);
  }

}
