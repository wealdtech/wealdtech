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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.wealdtech.TwoTuple;
import com.wealdtech.authentication.OAuth2Credentials;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 *
 */
public interface CalendarClient
{
  /**
   * Obtain an event in the primary calendar
   *
   * @param credentials credentials for the request
   * @param eventId the ID of the event to obtain
   * @return the obtained event, or {@code null} if no such event
   */
  @Nullable
  Event obtainEvent(OAuth2Credentials credentials, String eventId);

  /**
   * Obtain an event in a specific calendar
   *
   * @param credentials credentials for the request
   * @param calendarId the ID of the calendar in which to obtain the event
   * @param eventId the ID of the event to obtain
   * @return the obtained event, or {@code null} if no such event
   */
  @Nullable
  Event obtainEvent(OAuth2Credentials credentials, String calendarId, String eventId);

  /**
   * Obtain all events in the primary calendar for a given timeframe
   *
   * @param credentials credentials for the request
   * @param timeframe the timeframe over which to obtain the events
   * @return the obtained events
   */
  ImmutableList<Event> obtainEvents(OAuth2Credentials credentials, Range<DateTime> timeframe);

  /**
   * Obtain all events in a specific calendar for a given timeframe
   *
   * @param credentials credentials for the request
   * @param calendarId the ID of the calendar in which to obtain the event
   * @param timeframe the timeframe over which to obtain the events
   * @return the obtained events
   */
  ImmutableList<Event> obtainEvents(OAuth2Credentials credentials, String calendarId, Range<DateTime> timeframe);

  /**
   * Create an event in the primary calendar
   *
   * @param credentials credentials for the request
   * @param event the event to create
   * @return the created event, with updated fields where appropriate
   */
  Event createEvent(OAuth2Credentials credentials, Event event);

  /**
   * Create an event in a specific calendar
   *
   * @param credentials credentials for the request
   * @param calendarId the ID of the calendar in which to create the event
   * @param event the event to create
   * @return the created event, with updated fields where appropriate
   */
  Event createEvent(OAuth2Credentials credentials, String calendarId, Event event);

  /**
   * Delete an event in the primary calendar
   *
   * @param credentials credentials for the request
   * @param eventId the ID of the event to delete
   */
  void deleteEvent(OAuth2Credentials credentials, String eventId) throws IOException;

  /**
   * Delete an event in a specific calendar
   *
   * @param credentials credentials for the request
   * @param calendarId the ID of the calendar in which to create the event
   * @param eventId the ID of the event to delete
   */
  void deleteEvent(OAuth2Credentials credentials, String calendarId, String eventId) throws IOException;

  ImmutableList<Calendar> obtainCalendars(OAuth2Credentials credentials) throws IOException;

  /**
   * Obtain a range of times that are free, along with a level of suitability for each

   * @param credentials credentials for the request
   * @param timeframe the timeframe over which to obtain free times
   * @param duration the duration of the required range
   * @return a list of suitable ranges, ordered by suitability
   */
  ImmutableList<TwoTuple<Range<DateTime>, Long>> obtainFreeRanges(OAuth2Credentials credentials,
                                                                  Range<DateTime> timeframe,
                                                                  Range<DateTime> mask,
                                                                  Long duration);
}
