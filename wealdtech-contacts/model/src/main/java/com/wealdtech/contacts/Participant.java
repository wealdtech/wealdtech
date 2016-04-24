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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;
import com.wealdtech.contacts.handles.Handle;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A Participant is a combination of contact and relationship information that represents how to communicate with someone
 * in a particular context
 */
public class Participant extends WObject<Participant> implements Comparable<Participant>
{
  private static final String NAME = "name";
  private static final String FORMALITY = "formality";
  private static final String HANDLES = "handles";
  private static final String TIMEZONE = "timezone";

  @JsonCreator
  public Participant(final Map<String, Object> data)
  {
    super(data);
  }

  /**
   * @return the name that should be used for the participant
   */
  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }

  /**
   * @return the formality that should be used for the participant, in the range 0 - 100
   */
  @JsonIgnore
  public int getFormality() { return get(FORMALITY, Integer.class).get(); }

  private static final TypeReference<ImmutableList<? extends Handle>> HANDLES_TYPE_REF = new TypeReference<ImmutableList<? extends Handle>>(){};
  /**
   * @return the handles that can be used to contact the participant, in order of preference
   */
  @JsonIgnore
  public ImmutableList<? extends Handle> getHandles() { return get(HANDLES, HANDLES_TYPE_REF).get(); }

  /**
   * @return the timezone in which the participant resides (optional)
   */
  @JsonIgnore
  public Optional<DateTimeZone> getTimezone() { return get(TIMEZONE, DateTimeZone.class); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "Participant failed validation: missing name");
    checkState(exists(FORMALITY), "Participant failed validation: missing formality");
    checkState(exists(HANDLES), "Participant failed validation: missing handles");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Participant, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Participant prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P formality(final int formality)
    {
      data(FORMALITY, formality);
      return self();
    }

    public P handles(final List<? extends Handle> handles)
    {
      data(HANDLES, handles);
      return self();
    }

    public P timezone(final DateTimeZone timezone)
    {
      data(TIMEZONE, timezone);
      return self();
    }

    public Participant build()
    {
      return new Participant(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Participant prior)
  {
    return new Builder(prior);
  }
}
