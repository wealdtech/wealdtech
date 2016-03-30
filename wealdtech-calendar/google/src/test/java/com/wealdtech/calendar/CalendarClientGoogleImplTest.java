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
import com.wealdtech.WID;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.calendar.config.CalendarConfiguration;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.services.google.GoogleAccountsClient;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class CalendarClientGoogleImplTest
{
  @Test
  public void testOauthStep1() throws MalformedURLException
  {
    final CalendarConfiguration configuration =
        new ConfigurationSource<CalendarConfiguration>().getConfiguration("test-calendar.json", CalendarConfiguration.class);
    assertEquals(configuration.getOauth2Configuration().generateAuthorizationUrl().toString(), "https://accounts.google.com/o/oauth2/auth?client_id=1046543602732-d2ffn8iiq1na401k2830kud9ebdelj7j.apps.googleusercontent.com&response_type=code&scope=openid%20profile%20email%20https://www.googleapis.com/auth/calendar&redirect_uri=http://api.ellie.ai/oauth2/googlecalendar/callback&state=auth&access_type=offline");
  }

  @Test
  public void testObtainCalendars() throws IOException, GeneralSecurityException
  {
    final CalendarConfiguration configuration =
        new ConfigurationSource<CalendarConfiguration>().getConfiguration("test-calendar.json", CalendarConfiguration.class);

    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                                                 .name("Google calendar")
                                                                                 .accessToken("irrelevant")
                                                                                 .expires(DateTime.now().minusDays(1))
                                                                                 .refreshToken(
                                                                                     "1/hiV8axfKcuAlB9ZUgF6NaokllYp8PYSJSgMwWNIidWMMEudVrK5jSpoR30zcRFq6")
                                                                                 .build());
    final CalendarClient client = new CalendarClientGoogleImpl(configuration);
    ImmutableList<Calendar> calendars = client.obtainCalendars(credentials);
    // TODO assert calendars
  }

  @Test
  public void testCreateEvent() throws IOException, GeneralSecurityException
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName();

    final CalendarConfiguration configuration =
        new ConfigurationSource<CalendarConfiguration>().getConfiguration("test-calendar.json", CalendarConfiguration.class);

    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                                                 .name("Google calendar")
                                                                                 .accessToken("irrelevant")
                                                                                 .expires(DateTime.now().minusDays(1))
                                                                                 .refreshToken(
                                                                                     "1/hiV8axfKcuAlB9ZUgF6NaokllYp8PYSJSgMwWNIidWMMEudVrK5jSpoR30zcRFq6")
                                                                                 .build());

    final CalendarClient client = new CalendarClientGoogleImpl(configuration);

    Event event = null;
    try
    {
      final DateTime eventStart = DateTime.now().withTimeAtStartOfDay().plusDays(1).withHourOfDay(9);
      event = Event.builder()
                   .icalId(WID.<Event>generate().toString() + "@ellie.ai")
                   .sequence(0)
                   .summary(testName)
                   .startDateTime(eventStart)
                   .endDateTime(eventStart.plusHours(1))
                   .build();
      event = client.createEvent(credentials, event);

      // Attempt to fetch the event
      final Event remoteEvent = client.obtainEvent(credentials, event.getRemoteId().orNull());

      assertEquals(remoteEvent, event);
      System.err.println("Remote event is " + remoteEvent);
    }
    catch (final Exception e)
    {
      System.err.println(e);
      throw e;
    }
    finally
    {
      if (event != null)
      {
//        client.deleteEvent(credentials, event.getRemoteId().orNull());
      }
    }
  }

  // Helpers

  private Calendar obtainCalendarBySummary(final ImmutableList<Calendar> calendars, final String summary)
  {
    for (final Calendar calendar : calendars)
    {
      if (Objects.equals(calendar.getSummary(), summary))
      {
        return calendar;
      }
    }
    return null;
  }
}
