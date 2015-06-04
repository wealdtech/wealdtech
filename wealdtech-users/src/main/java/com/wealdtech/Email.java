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

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Email contains information about an email address associated with a user
 */
public class Email extends WObject<Email> implements Comparable<Email>
{
  private static final String ADDRESS = "address";
  private static final String PRIMARY = "primary";
  private static final String VERIFIED = "verified";

  @JsonCreator
  public Email(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(ADDRESS), "Email failed validation: must contain address");
    checkState(exists(PRIMARY), "Email failed validation: must contain primary flag");
    checkState(exists(VERIFIED), "Email failed validation: must contain verified flag");
  }

  @JsonIgnore
  public String getAddress()
  {
    return get(ADDRESS, String.class).get();
  }

  public boolean isPrimary()
  {
    return get(PRIMARY, Boolean.class).get();
  }

  public boolean isVerified()
  {
    return get(VERIFIED, Boolean.class).get();
  }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Email, P>
  {
    public Builder(){ super(); }

    public Builder(final Email prior)
    {
      super(prior);
    }

    public P address(final String address)
    {
      data(ADDRESS, address);
      return self();
    }

    public P primary(final Boolean primary)
    {
      data(PRIMARY, primary);
      return self();
    }

    public P verified(final Boolean verified)
    {
      data(VERIFIED, verified);
      return self();
    }

    public Email build(){ return new Email(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final Email prior)
  {
    return new Builder(prior);
  }
}
