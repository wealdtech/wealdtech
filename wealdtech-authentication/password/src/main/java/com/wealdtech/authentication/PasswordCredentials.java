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
 * Password-based credentials for authentication.
 * Password-based credentials are based on two pieces of information: a name, which identifies a user and a password, which
 * authenticates the user.  Both are required
 */
public class PasswordCredentials extends AbstractCredentials
{
  private static final Logger LOG = LoggerFactory.getLogger(PasswordCredentials.class);

  public static final String PASSWORD_CREDENTIALS = "Password";

  private static final String NAME = "name";
  private static final String PASSWORD = "password";

  @JsonCreator
  public PasswordCredentials(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "Password authentication method failed validation: must contain name");
    checkState(exists(PASSWORD), "Password authentication method failed validation: must contain password");
  }

  @JsonIgnore
  public String getType(){return PASSWORD_CREDENTIALS;}

  @JsonIgnore
  public String getName(){return get(NAME, String.class).get();}

  @JsonIgnore
  public String getPassword(){return get(PASSWORD, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends AbstractCredentials.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, PASSWORD_CREDENTIALS);
    }

    public Builder(final PasswordCredentials prior)
    {
      super(prior);
      data(TYPE, PASSWORD_CREDENTIALS);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P password(final String password)
    {
      data(PASSWORD, password);
      return self();
    }

    public PasswordCredentials build(){ return new PasswordCredentials(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final PasswordCredentials prior)
  {
    return new Builder(prior);
  }
}
