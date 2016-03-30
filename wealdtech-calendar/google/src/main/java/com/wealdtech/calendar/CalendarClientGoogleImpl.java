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

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.wealdtech.ServerError;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.calendar.config.CalendarConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.wealdtech.Preconditions.checkState;

/**
 *
 */
public class CalendarClientGoogleImpl implements CalendarClient
{
  private static final Logger LOG = LoggerFactory.getLogger(CalendarClientGoogleImpl.class);

  private final CalendarConfiguration configuration;

  private final HttpTransport httpTransport;

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static final String PRIMARY_CALENDAR_ID = "primary";

  public CalendarClientGoogleImpl(final CalendarConfiguration configuration) throws GeneralSecurityException, IOException
  {
    this.configuration = configuration;
    this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
  }

  @Override
  public Event createEvent(final OAuth2Credentials credentials, final Event event)
  {
    return createEvent(credentials, PRIMARY_CALENDAR_ID, event);
  }

  @Override
  public Event createEvent(final OAuth2Credentials credentials, final String calendarId, final Event event)
  {
    final Credential credential = generateCredential(credentials);

    final com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(configuration.getProductId()).build();

    final com.google.api.services.calendar.model.Event googleEvent = eventToGoogleEvent(event);
    try
    {
      final com.google.api.services.calendar.model.Event googleResultEvent = client.events().insert(calendarId, googleEvent).execute();
      return googleEventToEvent(googleResultEvent);
    }
    catch (final IOException ioe)
    {
      LOG.error("Exception when creating event: ", ioe);
      throw new ServerError("foo", ioe);
    }
  }

  @Override
  public Event obtainEvent(final OAuth2Credentials credentials, final String eventId)
  {
    return obtainEvent(credentials, PRIMARY_CALENDAR_ID, eventId);
  }

  @Override
  public Event obtainEvent(final OAuth2Credentials credentials, final String calendarId, final String eventId)
  {
    final Credential credential = generateCredential(credentials);

    final com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(configuration.getProductId()).build();

    try
    {
      return googleEventToEvent(client.events().get(calendarId, eventId).execute());
    }
    catch (final IOException ioe)
    {
      LOG.error("Exception when obtaining event: ", ioe);
      return null;
    }
  }

  @Override
  public final ImmutableList<Event> obtainEvents(final OAuth2Credentials credentials, final Range<DateTime> timeframe)
  {
    return obtainEvents(credentials, "primary", timeframe);
  }

  @Override
  public final ImmutableList<Event> obtainEvents(final OAuth2Credentials credentials, final String calendarId, final Range<DateTime> timeframe)
  {
    final Credential credential = generateCredential(credentials);

    final com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(configuration.getProductId()).build();

    final ImmutableList.Builder<Event> resultsB = ImmutableList.builder();
    try
    {
      final Events events = client.events()
                                  .list(calendarId)
                                  .setTimeMin(new com.google.api.client.util.DateTime(timeframe.lowerEndpoint().toDate()))
                                  .setTimeMax(new com.google.api.client.util.DateTime(timeframe.upperEndpoint().toDate()))
                                  .execute();
      for (com.google.api.services.calendar.model.Event event : events.getItems())
      {
        resultsB.add(googleEventToEvent(event));
      }
    }
    catch (final IOException ioe)
    {
      LOG.error("Exception when obtaining events: ", ioe);
    }

    return resultsB.build();
  }

  @Override
  public void deleteEvent(final OAuth2Credentials credentials, final String eventId) throws IOException
  {
    deleteEvent(credentials, PRIMARY_CALENDAR_ID, eventId);
  }

  @Override
  public void deleteEvent(final OAuth2Credentials credentials, final String calendarId, final String eventId) throws IOException
  {
    final Credential credential = generateCredential(credentials);

    final com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(configuration.getProductId()).build();
    client.events().delete(calendarId, eventId).execute();
  }

  private Credential generateCredential(final OAuth2Credentials credentials)
  {
    return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(httpTransport)
                                                                                .setJsonFactory(JSON_FACTORY)
                                                                                .setClientAuthentication(new OAuthParameters())
                                                                                .setTokenServerUrl(new GenericUrl("https://accounts.google.com/o/oauth2/token"))
                                                                                .build()
                                                                                .setAccessToken(credentials.getAccessToken())
                                                                                .setRefreshToken(credentials.getRefreshToken());
  }

  @Override
  public ImmutableList<Calendar> obtainCalendars(final OAuth2Credentials credentials) throws IOException
  {
    checkState(credentials != null, "Missing required credentials");

    final Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(httpTransport)
                                                                                                       .setJsonFactory(JSON_FACTORY)
                                                                                                       .setTokenServerUrl(new GenericUrl("https://accounts.google.com/o/oauth2/token"))
                                                                                                       .build()
                                                                                                       .setAccessToken(credentials.getAccessToken());

    final com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(configuration.getProductId()).build();

    CalendarList calendars = client.calendarList().list().execute();

    final ImmutableList.Builder<Calendar> builder = ImmutableList.builder();
    for (final CalendarListEntry entry : calendars.getItems())
    {
      builder.add(googleCalendarToCalendar(entry));
    }
    return builder.build();
  }

  /**
   * Helper to turn a Wealdtech calendar event in to a Google calendar event
   * @param event the Wealdtech calendar event
   * @return the Google calendar event
   */
  private com.google.api.services.calendar.model.Event eventToGoogleEvent(final Event event)
  {
    com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();
    if (event.getRemoteId().isPresent())
    {
      googleEvent.setId(event.getRemoteId().get());
    }
    else if (event.getIcalId().isPresent())
    {
      googleEvent.setICalUID(event.getIcalId().get());
    }
    else if (event.getId() != null)
    {
      googleEvent.setId(event.getId().toString());
    }
    googleEvent.setSummary(event.getSummary());
    if (event.getDescription().isPresent())
    {
      googleEvent.setDescription(event.getDescription().get());
    }
    if (event.getStartDate().isPresent())
    {
      googleEvent.setStart(new EventDateTime().setDate(new com.google.api.client.util.DateTime(event.getStartDate().get().toDate())));
    }
    else
    {
      googleEvent.setStart(new EventDateTime().setDateTime(
          new com.google.api.client.util.DateTime(event.getStartDateTime().get().toDate(),
                                                  event.getStartDateTime().get().getZone().toTimeZone())));
    }
    if (event.getEndDate().isPresent())
    {
      googleEvent.setEnd(new EventDateTime().setDate(new com.google.api.client.util.DateTime(event.getEndDate().get().toDate())));
    }
    else
    {
      googleEvent.setEnd(new EventDateTime().setDateTime(
          new com.google.api.client.util.DateTime(event.getEndDateTime().get().toDate(),
                                                  event.getEndDateTime().get().getZone().toTimeZone())));
    }

    return googleEvent;
  }

  /**
   * Helper to turn a Google calendar event in to a Wealdtech calendar event
   * @param event the Google calendar event
   * @return the Wealdtech calendar event
   */
  private Event googleEventToEvent(final com.google.api.services.calendar.model.Event event)
  {
    System.err.println("Google event is " + event.toString());
    final Event.Builder<?> builder = Event.builder();
    if (event.getId() != null)
    {
      builder.remoteId(event.getId());
    }
    if (event.getICalUID() != null)
    {
      builder.icalId(event.getICalUID());
    }
    builder.sequence(event.getSequence());
    builder.summary(event.getSummary());
    if (event.getDescription() != null)
    {
      builder.description(event.getDescription());
    }
    if (event.getStart().getDate() != null)
    {
      builder.startDate(new LocalDate(event.getStart().getDate()));
    }
    if (event.getStart().getDateTime() != null)
    {
      builder.startDateTime(new org.joda.time.DateTime(event.getStart().getDateTime().getValue(),
                                                       DateTimeZone.forID(event.getStart().getTimeZone())));
    }
    if (event.getEnd().getDate() != null)
    {
      builder.endDate(new LocalDate(event.getEnd().getDate()));
    }
    if (event.getEnd().getDateTime() != null)
    {
      builder.endDateTime(new org.joda.time.DateTime(event.getEnd().getDateTime().getValue(),
                                                       DateTimeZone.forID(event.getEnd().getTimeZone())));
    }

    return builder.build();
  }

  /**
   * Helper to turn a Wealdtech calendar in to a Google calendar
   * @param calendar the Wealdtech calendar calendar
   * @return the Google calendar calendar
   */
  private com.google.api.services.calendar.model.Calendar calendarToGoogleCalendar(final Calendar calendar)
  {
    com.google.api.services.calendar.model.Calendar googleCalendar = new com.google.api.services.calendar.model.Calendar();
    if (calendar.getRemoteId().isPresent())
    {
      googleCalendar.setId(calendar.getRemoteId().get());
    }
    googleCalendar.setSummary(calendar.getSummary());
    if (calendar.getDescription().isPresent())
    {
      googleCalendar.setDescription(calendar.getDescription().get());
    }

    return googleCalendar;
  }

  /**
   * Helper to turn a Google calendar calendar in to a Wealdtech calendar calendar
   * @param calendar the Google calendar calendar
   * @return the Wealdtech calendar calendar
   */
  private Calendar googleCalendarToCalendar(final com.google.api.services.calendar.model.CalendarListEntry calendar)
  {
    final Calendar.Builder<?> builder = Calendar.builder();
    if (calendar.getId() != null)
    {
      builder.remoteId(calendar.getId());
    }
    builder.summary(calendar.getSummary());
    if (calendar.getDescription() != null)
    {
      builder.description(calendar.getDescription());
    }

    return builder.build();
  }
}
