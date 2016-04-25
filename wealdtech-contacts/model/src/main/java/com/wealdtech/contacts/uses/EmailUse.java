/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.uses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Optional;
import com.wealdtech.DataError;

import java.util.Map;

/**
 * A use related to an email address.
 */
@JsonTypeName("email")
public class EmailUse extends Use<EmailUse> implements Comparable<EmailUse>
{
  private static final String _TYPE = "email";

  private static final String ADDRESS = "address";
  private static final String DISPLAY_NAME = "displayname";

  @JsonCreator
  public EmailUse(final Map<String, Object> data){ super(data); }

  @JsonIgnore
  public String getAddress() { return get(ADDRESS, String.class).get(); }

  @JsonIgnore
  public Optional<String> getDisplayName() { return get(DISPLAY_NAME, String.class); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(ADDRESS));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();

    if (!exists(ADDRESS))
    {
      throw new DataError.Missing("Email use failed validation: missing address");
    }
  }

  /**
   * Increase the familiarity of this use
   */
  public EmailUse increaseFamiliarity()
  {
    return EmailUse.builder(this).familiarity(getFamiliarity() + 1).build();
  }

  public static class Builder<P extends Builder<P>> extends Use.Builder<EmailUse, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final EmailUse prior)
    {
      super(prior);
    }

    public P address(final String address)
    {
      data(ADDRESS, address);
      return self();
    }

    public P displayName(final String displayName)
    {
      data(DISPLAY_NAME, displayName);
      return self();
    }

    public EmailUse build()
    {
      return new EmailUse(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final EmailUse prior)
  {
    return new Builder(prior);
  }

}
