/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Remote contains information about a user's ability to access a remote system
 */
public abstract class Remote<T extends Remote<T>> extends WObject<T> implements Comparable<T>
{
  private static final String REF = "ref";

  public Remote(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(REF), "Remote failed validation: must contain ref");
  }

  @JsonIgnore
  public String getRef()
  {
    return get(REF, String.class).get();
  }

  // Builder boilerplate
  public static class Builder<T extends Remote<T>, P extends Builder<T, P>> extends WObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P ref(final String ref)
    {
      data(REF, ref);
      return self();
    }
  }
}
