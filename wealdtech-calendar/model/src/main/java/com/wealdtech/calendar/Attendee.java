/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.WObject;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * An attendee for an event
 */
public class Attendee extends WObject<Attendee> implements Comparable<Attendee>
{
  private static final String NAME = "name";
  private static final String EMAIL = "email";
  private static final String MANDATORY = "mandatory";
  private static final String RESOURCE = "resource";
  private static final String STATUS = "status";

  @JsonCreator
  public Attendee(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    if (!data.containsKey(RESOURCE))
    {
      data.put(RESOURCE, false);
    }
    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "Attendee failed validation: must contain name");
    checkState(exists(EMAIL), "Attendee failed validation: must contain email");
    checkState(exists(MANDATORY), "Attendee failed validation: must contain mandatory");
    checkState(exists(RESOURCE), "Attendee failed validation: must contain resource");
    checkState(exists(STATUS), "Attendee failed validation: must contain status");
  }
  
  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }
  
  @JsonIgnore
  public String getEmail() { return get(EMAIL, String.class).get(); }
  
  @JsonIgnore
  public Boolean isMandatory() { return get(MANDATORY, Boolean.class).get(); }

  @JsonIgnore
  public Boolean isResource() { return get(RESOURCE, Boolean.class).get(); }
  
  @JsonIgnore
  public Status getStatus() { return get(STATUS, Status.class).get(); }
  
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Attendee, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Attendee prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P email(final String email)
    {
      data(EMAIL, email);
      return self();
    }

    public P mandatory(final Boolean mandatory)
    {
      data(MANDATORY, mandatory);
      return self();
    }

    public P resource(final Boolean resource)
    {
      data(RESOURCE, resource);
      return self();
    }

    public P status(final Status status)
    {
      data(STATUS, status);
      return self();
    }

    public Attendee build()
    {
      return new Attendee(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Attendee prior)
  {
    return new Builder(prior);
  }

  /**
   * The status of an attendee: unknown, declined, tentative, accepted
   */
  public static enum Status
  {
    /**
     * Unknown
     */
    UNKNOWN(-1)
    /**
     * Declined
     */
    ,DECLINED(1)
    /**
     * Tentative
     */
    ,TENTATIVE(2)
    /**
     * Accepted
     */
    ,ACCEPTED(3);

    public final int val;

    private Status(final int val)
    {
      this.val = val;
    }

    private static final ImmutableSortedMap<Integer, Status> _VALMAP;

    static
    {
      final Map<Integer, Status> levelMap = Maps.newHashMap();
      for (final Status status : Status.values())
      {
        levelMap.put(status.val, status);
      }
      _VALMAP = ImmutableSortedMap.copyOf(levelMap);
    }

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }

    @JsonCreator
    public static Status fromString(final String val)
    {
      try
      {
        return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("An attendee status \"" + val + "\" supplied is invalid");
      }
    }

    public static Status fromInt(final Integer val)
    {
      checkNotNull(val, "Attendee status not supplied");
      final Status state = _VALMAP.get(val);
      checkNotNull(state, "Attendee status is invalid");
      return state;
    }
  }
}
