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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.wealdtech.DataError;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

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
  private static final String STATUS = "status";
  private static final String TRANSPARENCY = "transparency";
  private static final String ATTENDEES = "attendees";

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

  @JsonIgnore
  public Transparency getTransparency() { return get(TRANSPARENCY, Transparency.class).get(); }

  @JsonIgnore
  public Status getStatus() { return get(STATUS, Status.class).get(); }

  private static final TypeReference<ImmutableSet<Attendee>> ATTENDEES_TYPE_REF = new TypeReference<ImmutableSet<Attendee>>(){};
  @JsonIgnore
  public ImmutableSet<Attendee> getAttendees() { return get(ATTENDEES, ATTENDEES_TYPE_REF).or(ImmutableSet.<Attendee>of()); }

  /**
   * Create a list of events from this event over a given timeframe.  Non-recurring events will either return 0 or 1 events and
   * recurring events will return any number of events.  All events returned will be marked as instantiated.
   * @return A list of instantiated events
   */
  @JsonIgnore
  public ImmutableList<Event> getInstantiatedEvents(final Range<DateTime> timeframe)
  {
    final ImmutableList.Builder<Event> builder = ImmutableList.builder();

    // TODO loop this for recurring events

    DateTime start = exists(START_DATE) ? getStartDate().get().toDateTimeAtStartOfDay() : getStartDateTime().get();
    DateTime end = exists(END_DATE) ? getEndDate().get().toDateTimeAtStartOfDay() : getEndDateTime().get();

    // We can't use isConnected() as that only works for closed ranges and we use closedopen ranges, so do it manually...
    if (timeframe.lowerEndpoint().getMillis() < end.getMillis() && timeframe.upperEndpoint().getMillis() > start.getMillis())
    {
      builder.add(this);
      // TODO add instantiated flag
    }

    return builder.build();
  }

  @JsonIgnore
  public Range<DateTime> getTimeframe()
  {
    final DateTime start = exists(START_DATE) ? getStartDate().get().toDateTimeAtStartOfDay() : getStartDateTime().get();
    final DateTime end = exists(END_DATE) ? getEndDate().get().toDateTimeAtStartOfDay() : getEndDateTime().get();
    return Range.closedOpen(start, end);
  }

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
    // And if we have a local ID but no iCal ID then create one
    else if (icalId == null && data.get(ID) != null)
    {
      data.put(ICAL_ID, data.get(ID).toString() + "@ellie.ai");
    }

    // Default sequence to 0
    if (!data.containsKey(SEQUENCE))
    {
      data.put(SEQUENCE, 0);
    }

    // Default transparency to opaque
    if (!data.containsKey(TRANSPARENCY))
    {
      data.put(TRANSPARENCY, Transparency.OPAQUE);
    }

    // Default status to confirmed
    if (!data.containsKey(STATUS))
    {
      data.put(STATUS, Status.CONFIRMED);
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
    if (!exists(TRANSPARENCY)) { throw new DataError.Missing("Event needs 'transparency' information"); }
    if (!exists(STATUS)) { throw new DataError.Missing("Event needs 'status' information"); }
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

    public P status(final Status status)
    {
      data(STATUS, status);
      return self();
    }

    public P transparency(final Transparency transparency)
    {
      data(TRANSPARENCY, transparency);
      return self();
    }

    public P attendees(final ImmutableSet<Attendee> attendees)
    {
      data(ATTENDEES, attendees);
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

  /**
   * The status of an event: cancelled, tentative, confirmed
   */
  public static enum Status
  {
    /**
     * Cancelled
     */
    CANCELLED(1)
    /**
     * Tentative
     */
    , TENTATIVE(2)
    /**
     * Confirmed
     */
    , CONFIRMED(3);

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
        throw new DataError.Bad("An event status \"" + val + "\" supplied is invalid");
      }
    }

    public static Status fromInt(final Integer val)
    {
      checkNotNull(val, "Event status not supplied");
      final Status state = _VALMAP.get(val);
      checkNotNull(state, "Event status is invalid");
      return state;
    }
  }

  /**
   * The transparency of an event: transparent, opaque
   */
  public static enum Transparency
  {
    /**
     * Transparent
     */
    TRANSPARENT(1)
    /**
     * Opaque
     */
    , OPAQUE(2);

    public final int val;

    private Transparency(final int val)
    {
      this.val = val;
    }

    private static final ImmutableSortedMap<Integer, Transparency> _VALMAP;

    static
    {
      final Map<Integer, Transparency> levelMap = Maps.newHashMap();
      for (final Transparency transparency : Transparency.values())
      {
        levelMap.put(transparency.val, transparency);
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
    public static Transparency fromString(final String val)
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
        throw new DataError.Bad("A transparency \"" + val + "\" supplied is invalid");
      }
    }

    public static Transparency fromInt(final Integer val)
    {
      checkNotNull(val, "Transparency not supplied");
      final Transparency state = _VALMAP.get(val);
      checkNotNull(state, "Transparency is invalid");
      return state;
    }
  }
}
