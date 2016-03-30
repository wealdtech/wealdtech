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
import com.google.common.base.Optional;
import com.wealdtech.DataError;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Map;

/**
 * An event in a calendar
 */
public class Event extends WObject<Event> implements Comparable<Event>
{
  private static final String REMOTE_ID = "remoteid";
  private static final String ICAL_ID = "icalid";
  private static final String SEQUENCE = "sequence";
  private static final String SUMMARY = "summary";
  private static final String DESCRIPTION = "description";
  private static final String START_DATE= "startdate";
  private static final String START_DATETIME= "startdatetime";
  private static final String END_DATE= "enddate";
  private static final String END_DATETIME= "enddatetime";

  @JsonCreator
  public Event(final Map<String, Object> data)
  {
    super(data);
  }

  /**
   * @return the remote ID.  The remote ID is the UID as created by the remote service
   */
  @JsonIgnore
  public Optional<String> getRemoteId() { return get(REMOTE_ID, String.class); }

  /**
   * @return the iCal ID.  The iCal ID is the ID of the overarching iCal event (which can be the same for multiple events if they form part of a recurrance)
   */
  @JsonIgnore
  public Optional<String> getIcalId() { return get(ICAL_ID, String.class); }

  @JsonIgnore
  public String getSummary() { return get(SUMMARY, String.class).get(); }

  @JsonIgnore
  public Optional<String> getDescription() { return get(DESCRIPTION, String.class); }

  @JsonIgnore
  public Optional<LocalDate> getStartDate() { return get(START_DATE, LocalDate.class); }

  @JsonIgnore
  public Optional<DateTime> getStartDateTime() { return get(START_DATETIME, DateTime.class); }

  @JsonIgnore
  public Optional<LocalDate> getEndDate() { return get(END_DATE, LocalDate.class); }

  @JsonIgnore
  public Optional<DateTime> getEndDateTime() { return get(END_DATETIME, DateTime.class); }

  @JsonIgnore
  public Integer getSequence() { return get(SEQUENCE, Integer.class).get(); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // If we have a IcalID that ends in "@ellie.ai" we need to use the main text as our identifier
    final String icalId = (String)data.get(ICAL_ID);
    if (icalId != null && icalId.endsWith("@ellie.ai"))
    {
      data.put(ID, WID.<Event>fromString(icalId.replace("@ellie.ai", "")));
    }

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(SEQUENCE)) { throw new DataError.Missing("Event needs 'sequence' information"); }
    if (!exists(SUMMARY)) { throw new DataError.Missing("Event needs 'summary' information"); }
    if (!exists(START_DATE) && !exists(START_DATETIME)) { throw new DataError.Missing("Event needs 'start date' or 'start datetime' information"); }
    // TODO more validation when we add recurrence
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Event, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Event prior)
    {
      super(prior);
    }

    public P remoteId(final String remoteId)
    {
      data(REMOTE_ID, remoteId);
      return self();
    }

    public P icalId(final String icalId)
    {
      data(ICAL_ID, icalId);
      return self();
    }

    public P sequence(final Integer sequence)
    {
      data(SEQUENCE, sequence);
      return self();
    }

    public P summary(final String summary)
    {
      data(SUMMARY, summary);
      return self();
    }

    public P description(final String description)
    {
      data(DESCRIPTION, description);
      return self();
    }

    public P startDate(final LocalDate startDate)
    {
      data(START_DATE, startDate);
      return self();
    }

    public P startDateTime(final DateTime startDateTime)
    {
      data(START_DATETIME, startDateTime);
      return self();
    }

    public P endDate(final LocalDate endDate)
    {
      data(END_DATE, endDate);
      return self();
    }

    public P endDateTime(final DateTime endDateTime)
    {
      data(END_DATETIME, endDateTime);
      return self();
    }

    public Event build()
    {
      return new Event(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Event prior)
  {
    return new Builder(prior);
  }

  }
