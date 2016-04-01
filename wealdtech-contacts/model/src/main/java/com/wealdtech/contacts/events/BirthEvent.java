/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.joda.time.LocalDate;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * An event relating to a contact's birth
 */
@JsonTypeName("birth")
public class BirthEvent extends Event<BirthEvent> implements Comparable<BirthEvent>
{
  private static final String _TYPE = "birth";

  private static final String DATE = "date";

  @JsonIgnore
  public LocalDate getDate() { return get(DATE, LocalDate.class).get(); }

  @JsonCreator
  public BirthEvent(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(DATE));

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(DATE), "Birth event failed validation: must contain date");
  }

  public static class Builder<P extends Builder<P>> extends Event.Builder<BirthEvent, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final BirthEvent prior)
    {
      super(prior);
    }

    public P date(final LocalDate date)
    {
      data(DATE, date);
      return self();
    }

    public BirthEvent build()
    {
      return new BirthEvent(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final BirthEvent prior)
  {
    return new Builder(prior);
  }

}
