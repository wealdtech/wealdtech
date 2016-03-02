/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Interact with the Google Calendar API
 */
public interface GoogleCalendarService
{
  /**
   * Obtain events from a given calendar
   *
   * @param calendarId the Id of the calendar for which to obtain the events
   *
   * @return
   */
  @GET("/{calendarid}/events")
  GenericWObject getEvents(@Path("calendarid") final String calendarId);

  /**
   * Obtain a specific event from a given calendar
   *
   * @param calendarId the Id of the calendar for which to obtain the event
   * @param eventId the Id of the event
   *
   * @return
   */
  @GET("/{calendarid}/events/{eventid}")
  GenericWObject getEvent(@Path("calendarid") final String calendarId,
                          @Path("eventid") final String eventId);

  /**
   * Create an event in a given calendar
   */
  @POST("/{calendarid}/events")
  Response createMessage(@Path("calendarid") final String calendarId,
                         @Body final GoogleCalendarEvent event);
}
