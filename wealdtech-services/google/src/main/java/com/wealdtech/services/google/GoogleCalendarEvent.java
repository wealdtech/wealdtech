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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * An event in the google calendar format
 */
public class GoogleCalendarEvent extends WObject<GoogleCalendarEvent> implements Comparable<GoogleCalendarEvent>
{
  @JsonCreator
  public GoogleCalendarEvent(final Map<String, Object> data)
  {
    super(data);
  }

}
