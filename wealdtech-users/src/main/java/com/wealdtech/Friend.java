/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech; // NOPMD

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Friend contains a subset of user information for displaying friends
 */
public class Friend extends WObject<Friend> implements Comparable<Friend>
{
  private static final Logger LOG = LoggerFactory.getLogger(Friend.class);

  private static final String NAME = "name";
  private static final String AVATAR = "avatar";

  @JsonCreator
  public Friend(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(ID), "Friend failed validation: must contain ID");
    checkState(exists(NAME), "Friend failed validation: must contain name");
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<Friend> getId(){ return super.getId(); }

  @JsonIgnore
  public String getName(){ return get(NAME, String.class).get(); }

  @JsonIgnore
  public Optional<String> getAvatar(){ return get(AVATAR, String.class); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Friend, P>
  {
    public Builder(){ super(); }

    public Builder(final Friend prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P avatar(final String avatar)
    {
      data(AVATAR, avatar);
      return self();
    }

    public Friend build(){ return new Friend(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final Friend prior)
  {
    return new Builder(prior);
  }
}
