/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * A contact, including details of their relationship to the acquiring user.
 */
public class Contact extends WObject<Contact> implements Comparable<Contact>
{
  private static final String NAME = "name";

  @JsonCreator
  public Contact(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<String> getName() { return get(NAME, String.class); }

  @Override
  protected void validate()
  {
    super.validate();
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Contact, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Contact prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }
    public Contact build()
    {
      return new Contact(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Contact prior)
  {
    return new Builder(prior);
  }

  }
