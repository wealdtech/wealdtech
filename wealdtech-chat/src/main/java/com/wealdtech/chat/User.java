/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.DataError;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A user in the chat system.
 */
public class User extends ChatObject<User> implements Comparable<User>
{
  private static final Logger LOG = LoggerFactory.getLogger(User.class);

  private static final String NAME = "name";

  @JsonCreator
  public User(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    super.validate();
    if (!exists(NAME))
    {
      throw new DataError.Missing("User needs 'name' information");
    }
  }

  @JsonIgnore
  public String getUseName()
  {
    return get(NAME, String.class).get();
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<User, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final User prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public User build()
    {
      return new User(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final User prior)
  {
    return new Builder(prior);
  }
}
