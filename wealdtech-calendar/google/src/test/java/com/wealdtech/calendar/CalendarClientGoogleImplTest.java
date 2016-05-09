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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.calendar.config.CalendarConfiguration;
import com.wealdtech.services.google.GoogleAccountsClient;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 *
 */
public class CalendarClientGoogleImplTest
{
  private static final ImmutableList<String> CALENDAR_SCOPES =
      ImmutableList.of("openid", "profile", "mail", "https://www.googleapis.com/auth/calendar");

  private static final String REFRESH_TOKEN = System.getenv("calendar_test_user_refresh_token");

//  @Test
//  public void testObtainCalendars() throws IOException, GeneralSecurityException
//  {
//    final CalendarConfiguration configuration = CalendarConfiguration.fromEnv("calendar_test");
//
//    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
//    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
//                                                                                 .name("Google calendar")
//                                                                                 .accessToken("irrelevant")
//                                                                                 .expires(DateTime.now().minusDays(1))
//                                                                                 .refreshToken(REFRESH_TOKEN)
//                                                                                 .build());
//    final CalendarClient client = new CalendarClientGoogleImpl(configuration);
//    ImmutableList<Calendar> calendars = client.obtainCalendars(credentials);
//    // TODO assert calendars
//  }
//
//  @Test
//  public void testCreateEvent() throws IOException, GeneralSecurityException
//  {
//    final String testName = new Object() {}.getClass().getEnclosingMethod().getName();
//
//    final CalendarConfiguration configuration = CalendarConfiguration.fromEnv("calendar_test");
//
//    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
//    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
//                                                                                 .name("Google calendar")
//                                                                                 .accessToken("irrelevant")
//                                                                                 .expires(DateTime.now().minusDays(1))
//                                                                                 .refreshToken(REFRESH_TOKEN)
//                                                                                 .build());
//
//    final CalendarClient client = new CalendarClientGoogleImpl(configuration);
//
//    Event event = null;
//    try
//    {
//      final DateTime eventStart = DateTime.now().withTimeAtStartOfDay().plusDays(1).withHourOfDay(9);
//      event = Event.builder()
//                   .icalId(WID.<Event>generate().toString() + "@ellie.ai")
//                   .sequence(0)
//                   .summary(testName)
//                   .startDateTime(eventStart)
//                   .endDateTime(eventStart.plusHours(1))
//                   .build();
//      event = client.createEvent(credentials, event);
//
//      // Attempt to fetch the event
//      final Event remoteEvent = client.obtainEvent(credentials, event.getRemoteId().orNull());
//
//      assertEquals(remoteEvent, event);
//      System.err.println("Remote event is " + remoteEvent);
//    }
//    catch (final Exception e)
//    {
//      System.err.println(e);
//      throw e;
//    }
//    finally
//    {
//      if (event != null)
//      {
//        client.deleteEvent(credentials, event.getRemoteId().orNull());
//      }
//    }
//  }

  @Test
  public void testObtainFreeRanges() throws IOException, GeneralSecurityException
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName();

    final CalendarConfiguration configuration = CalendarConfiguration.fromEnv("calendar_test");

    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                                                 .name("Google calendar")
                                                                                 .accessToken("irrelevant")
                                                                                 .scopes(ImmutableSet.of(
                                                                                     "https://www.googleapis.com/auth/calendar"))
                                                                                 .expires(DateTime.now().minusDays(1))
                                                                                 .refreshToken(REFRESH_TOKEN)
                                                                                 .build());

    final CalendarClient client = new CalendarClientGoogleImpl(configuration);

    client.obtainFreeRanges(credentials, Range.closedOpen(new DateTime(), new DateTime().plusDays(7)), null, 3600L);
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
