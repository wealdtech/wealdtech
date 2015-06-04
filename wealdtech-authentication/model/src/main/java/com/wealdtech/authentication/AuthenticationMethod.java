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
import com.google.common.base.Optional;
import com.wealdtech.WObject;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * An authentication method defines a way in which credentials can be used to authenticate against an entity
 */
public class AuthenticationMethod extends WObject<AuthenticationMethod>
{
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationMethod.class);

  protected static final String TYPE = "type";
  private static final String SCOPE = "scope";
  private static final String DESCRIPTION = "description";
  private static final String EXPIRY = "expiry";

  @JsonCreator
  public AuthenticationMethod(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {

    return data;
  }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Authentication method failed validation: must contain type");
    checkState(exists(SCOPE), "Authentication method failed validation: must contain scope");
  }

  @JsonIgnore
  public String getType(){return get(TYPE, String.class).get();}

  @JsonIgnore
  public AuthorisationScope getScope(){return get(SCOPE, AuthorisationScope.class).get();}

  @JsonIgnore
  public Optional<String> getDescription(){ return get(DESCRIPTION, String.class); }

  @JsonIgnore
  public Optional<LocalDateTime> getExpiry(){return get(EXPIRY, LocalDateTime.class);}

  /**
   * Check if an authentication method has expired
   *
   * @return <code>true</code> if it has expired, <code>false</code> otherwise
   */
  public boolean hasExpired()
  {
    return this.getExpiry().isPresent() && this.getExpiry().get().isBefore(LocalDateTime.now());
  }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<AuthenticationMethod, P>
  {
    public Builder(){ super(); }

    public Builder(final AuthenticationMethod prior)
    {
      super(prior);
    }

    public P scope(final AuthorisationScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public P description(final String description)
    {
      data(DESCRIPTION, description);
      return self();
    }

    public P expiry(final LocalDateTime expiry)
    {
      data(EXPIRY, expiry);
      return self();
    }

    public AuthenticationMethod build(){ return new AuthenticationMethod(data) {}; }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final AuthenticationMethod prior)
  {
    return new Builder(prior);
  }
}
