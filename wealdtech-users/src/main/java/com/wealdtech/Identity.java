/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.wealdtech.authentication.AuthorisationScope;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Details of a user's identity
 */
public class Identity extends WObject<Identity> implements Comparable<Identity>
{
  private static final String IDENTITY_ID = "identityid";
  private static final String SCOPE = "scope";

  @JsonCreator
  public Identity(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  private static final TypeReference<WID<Identity>> IDENTITY_ID_TYPEREF = new TypeReference<WID<Identity>>() {};

  @JsonIgnore
  public WID<Identity> getIdentityId(){ return get(IDENTITY_ID, IDENTITY_ID_TYPEREF).get(); }

  @JsonIgnore
  public AuthorisationScope getScope(){ return get(SCOPE, AuthorisationScope.class).get(); }

  protected void validate()
  {
    checkState(exists(IDENTITY_ID), "Identity must contain identity ID (" + getAllData() + ")");
    checkState(exists(SCOPE), "Identity must contain scope (" + getAllData() + ")");
  }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Identity, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Identity prior)
    {
      super(prior);
    }

    public P identityId(final WID<Identity> identityId)
    {
      data(IDENTITY_ID, identityId);
      return self();
    }

    public P scope(final AuthorisationScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public Identity build()
    {
      return new Identity(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Identity prior)
  {
    return new Builder(prior);
  }
}
