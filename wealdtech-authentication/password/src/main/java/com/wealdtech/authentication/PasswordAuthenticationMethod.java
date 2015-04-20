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
import com.wealdtech.utils.Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A password-based authentication method
 */
public class PasswordAuthenticationMethod extends AuthenticationMethod
{
  private static final Logger LOG = LoggerFactory.getLogger(PasswordAuthenticationMethod.class);

  public static final String PASSWORD_AUTHENTICATION = "Password";

  private static final String PASSWORD = "password";

  @JsonCreator
  public PasswordAuthenticationMethod(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Ensure that the password is hashed
    final String password = (String)data.get(PASSWORD);
    if (!Crypt.isHashed(password))
    {
      data.put(PASSWORD, Crypt.hash(password));
    }

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(PASSWORD), "Password authentication method failed validation: must contain password");
  }

  @JsonIgnore
  public String getType(){return PASSWORD_AUTHENTICATION;}

  @JsonIgnore
  public String getPassword(){return get(PASSWORD, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends AuthenticationMethod.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, PASSWORD_AUTHENTICATION);
    }

    public Builder(final PasswordAuthenticationMethod prior)
    {
      super(prior);
      data(TYPE, PASSWORD_AUTHENTICATION);
    }

    public P password(final String password)
    {
      data(PASSWORD, password);
      return self();
    }

    public PasswordAuthenticationMethod build(){ return new PasswordAuthenticationMethod(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final PasswordAuthenticationMethod prior)
  {
    return new Builder(prior);
  }
}
