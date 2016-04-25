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
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.contacts.uses.Use;
import org.joda.time.DateTimeZone;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A Participant is a combination of contact and relationship information that represents how to communicate with someone
 * in a particular context
 */
public class Participant extends WObject<Participant> implements Comparable<Participant>
{
  private static final String RELATIONSHIP_ID = "relationshipid";
  private static final String USES = "uses";
  private static final String TIMEZONE = "timezone";

  @JsonCreator
  public Participant(final Map<String, Object> data)
  {
    super(data);
  }

  private static final TypeReference<WID<Relationship>> RELATIONSHIP_ID_TYPE_REF = new TypeReference<WID<Relationship>>(){};
  /**
   * @return the ID of the relationship from which this participant came
   */
  @JsonIgnore
  public WID<Relationship> getRelationshipId() { return get(RELATIONSHIP_ID, RELATIONSHIP_ID_TYPE_REF).get(); }

  private static final TypeReference<ImmutableSet<? extends Use>> USES_TYPE_REF = new TypeReference<ImmutableSet<? extends Use>>(){};
  /**
   * @return the handles that can be used to contact the participant
   */
  @JsonIgnore
  public ImmutableSet<? extends Use> getUses() { return get(USES, USES_TYPE_REF).get(); }

  /**
   * @return the timezone in which the participant resides (optional)
   */
  @JsonIgnore
  public Optional<DateTimeZone> getTimezone() { return get(TIMEZONE, DateTimeZone.class); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(RELATIONSHIP_ID), "Participant failed validation: missing relationship ID");
    checkState(exists(USES), "Participant failed validation: missing uses");
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

    public P relationshipId(final WID<Relationship> relationshipId)
    {
      data(RELATIONSHIP_ID, relationshipId);
      return self();
    }

    public P uses(final ImmutableSet<? extends Use> uses)
    {
      data(USES, uses);
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
