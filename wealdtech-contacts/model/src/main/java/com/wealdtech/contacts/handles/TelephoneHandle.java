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
 * A handle that defines a contact's telephone number.
 */
@JsonTypeName("telephone")
public class TelephoneHandle extends Handle<TelephoneHandle> implements Comparable<TelephoneHandle>
{
  private static final String _TYPE = "telephone";

  private static final String NUMBER = "number";

  @JsonCreator
  public TelephoneHandle(final Map<String, Object> data){ super(data); }

  @JsonIgnore
  public String getNumber() { return get(NUMBER, String.class).get(); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(NUMBER));

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NUMBER), "Telephone handle failed validation: must contain number");
  }

  public static class Builder<P extends Builder<P>> extends Handle.Builder<TelephoneHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final TelephoneHandle prior)
    {
      super(prior);
    }

    public P number(final String number)
    {
      data(NUMBER, number);
      return self();
    }

    public TelephoneHandle build()
    {
      return new TelephoneHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final TelephoneHandle prior)
  {
    return new Builder(prior);
  }

}
