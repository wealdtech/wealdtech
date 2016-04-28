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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Participant;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * The various elements of a request required for creating an event
 */
public class CreateEventRequest extends WObject<CreateEventRequest> implements Comparable<CreateEventRequest>
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateEventRequest.class);

  private static final String ORIGINATOR_ID = "originatorid";
  private static final String NAME = "name";
  private static final String CONTEXT = "context";
  private static final String START_DATE_TIME = "startdatetime";
  private static final String END_DATE_TIME = "enddatetime";
  private static final String START_DATE = "startdate";
  private static final String END_DATE = "enddate";
  private static final String PARTICIPANTS = "participants";
  private static final TypeReference<WID<User>> ORIGINATOR_ID_TYPE_REF = new TypeReference<WID<User>>(){};
  private static final TypeReference<ImmutableList<Participant>> PARTICIPANTS_TYPE_REF = new TypeReference<ImmutableList<Participant>>(){};

  @JsonCreator
  public CreateEventRequest(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  public void validate()
  {
    super.validate();
    checkState(exists(ORIGINATOR_ID), "CreateEventRequest failed validation: requires originatorid");
  }

  @JsonIgnore
  public WID<User> getOriginatorId() { return get(ORIGINATOR_ID, ORIGINATOR_ID_TYPE_REF).get(); }

  /**
   *  @return the context of the event
   */
  @JsonIgnore
  public Optional<Context> getContext()
  {
    return get(CONTEXT, Context.class);
  }

  /**
   *  @return the name of the event
   */
  @JsonIgnore
  public Optional<String> getName()
  {
    return get(NAME, String.class);
  }

  /**
   *  @return the start date/time of the event
   */
  @JsonIgnore
  public Optional<DateTime> getStartDateTime()
  {
    return get(START_DATE_TIME, DateTime.class);
  }

  /**
   *  @return the end date/time of the event
   */
  @JsonIgnore
  public Optional<DateTime> getEndDateTime()
  {
    return get(END_DATE_TIME, DateTime.class);
  }

  /**
   *  @return the start date of the event
   */
  @JsonIgnore
  public Optional<LocalDate> getStartDate()
  {
    return get(START_DATE, LocalDate.class);
  }

  /**
   * @return the end date/time of the event
   */
  @JsonIgnore
  public Optional<LocalDate> getEndDate()
  {
    return get(END_DATE, LocalDate.class);
  }

  /**
   * @return the list of participants for the event
   */
  @JsonIgnore
  public Optional<ImmutableList<Participant>> getParticipants()
  {
    return get(PARTICIPANTS, PARTICIPANTS_TYPE_REF);
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<CreateEventRequest, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final CreateEventRequest prior)
    {
      super(prior);
    }

    public P originatorId(final WID<User> originatorId)
    {
      data(ORIGINATOR_ID, originatorId);
      return self();
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P context(final Context context)
    {
      data(CONTEXT, context);
      return self();
    }

    public P startDateTime(final DateTime startDateTime)
    {
      data(START_DATE_TIME, startDateTime);
      return self();
    }

    public P endDateTime(final DateTime endDateTime)
    {
      data(END_DATE_TIME, endDateTime);
      return self();
    }

    public P startDate(final LocalDate startDate)
    {
      data(START_DATE, startDate);
      return self();
    }

    public P endDate(final LocalDate endDate)
    {
      data(END_DATE, endDate);
      return self();
    }

    public P participants(final ImmutableList<Participant> participants)
    {
      data(PARTICIPANTS, participants);
      return self();
    }

    public CreateEventRequest build()
    {
      return new CreateEventRequest(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final CreateEventRequest prior)
  {
    return new Builder(prior);
  }
}
