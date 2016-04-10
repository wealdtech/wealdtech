/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.handles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A handle that defines a contact's unofficial name.
 */
@JsonTypeName("nickname")
public class NickNameHandle extends Handle<NickNameHandle> implements Comparable<NickNameHandle>
{
  private static final String _TYPE = "nickname";

  private static final String NAME = "name";

  @JsonIgnore
  public String getDomain() { return get(NAME, String.class).get(); }

  @JsonCreator
  public NickNameHandle(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(NAME));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "Nickame handle failed validation: must contain name");
  }

  public static class Builder<P extends Builder<P>> extends Handle.Builder<NickNameHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final NickNameHandle prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public NickNameHandle build()
    {
      return new NickNameHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final NickNameHandle prior)
  {
    return new Builder(prior);
  }

}
