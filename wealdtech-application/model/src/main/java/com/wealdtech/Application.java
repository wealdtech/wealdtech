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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * An application.  The application defines a logical separation of elements
 */
public class Application extends WObject<Application> implements Comparable<Application>
{
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  private static final String NAME = "name";
  private static final String OWNER_ID = "ownerid";

  @JsonCreator
  public Application(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    checkState(exists(ID), "Application failed validation: ID is required");
    checkState(exists(NAME), "Application failed validation: name is required");
    checkState(exists(OWNER_ID), "Application failed validation: owner ID is required");
  }

  @Override
  @JsonIgnore
  @Nonnull
  public WID<Application> getId() { return super.getId(); }

  @JsonIgnore
  public String getName()
  {
    return get(NAME, String.class).get();
  }

  @JsonIgnore
  public String getOwnerId()
  {
    return get(OWNER_ID, String.class).get();
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Application, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Application prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P ownerId(final String ownerId)
    {
      data(OWNER_ID, ownerId);
      return self();
    }

    public Application build()
    {
      return new Application(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Application prior)
  {
    return new Builder(prior);
  }
}
