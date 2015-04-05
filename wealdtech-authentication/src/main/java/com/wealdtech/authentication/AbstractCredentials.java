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
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

public abstract class AbstractCredentials extends WObject<AbstractCredentials> implements Credentials
{
  protected static final String TYPE = "type";

  @JsonCreator
  public AbstractCredentials(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Credentials failed validation: must contain type");
  }

  @Override
  @JsonIgnore
  public String getType(){return get(TYPE, String.class).get();}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<AbstractCredentials, P>
  {
    public Builder() { super(); }

    public Builder(final AbstractCredentials prior)
    {
      super(prior);
    }

    public P type(final String type)
    {
      data(TYPE, type);
      return self();
    }
  }

  public static Builder<?> builder() { return new Builder(); }

  public static Builder<?> builder(final AbstractCredentials prior)
  {
    return new Builder(prior);
  }
}
