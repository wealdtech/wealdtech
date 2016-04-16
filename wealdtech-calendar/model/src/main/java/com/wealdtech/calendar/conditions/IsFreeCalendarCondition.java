/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.calendar.conditions;

import com.google.common.collect.Range;
import com.wealdtech.calendar.Calendar;
import com.wealdtech.calendar.Event;
import org.joda.time.DateTime;

/**
 * Condition to check if a timeframe in a calendar is free
 */
public class IsFreeCalendarCondition extends CalendarCondition
{
  private Range<DateTime> timeframe;

  public IsFreeCalendarCondition(final Calendar calendar, final Range<DateTime> timeframe)
  {
    super(calendar);
    this.timeframe = timeframe;
  }

  @Override
  public Boolean evaluate()
  {
    // Short-circuit if the timeframe is empty
    if (timeframe.lowerEndpoint().getMillis() == timeframe.upperEndpoint().getMillis())
    {
      return true;
    }

    for (final Event event : calendar.getEvents())
    {
      // We obtain instantiated events over the supplied timeframe
      for (final Event instantiatedEvent : event.getInstantiatedEvents(timeframe))
      {
        // For the time to be free the event must be opaque and not cancelled
        if (instantiatedEvent.getTransparency() == Event.Transparency.OPAQUE &&
            instantiatedEvent.getStatus() != Event.Status.CANCELLED)
        {
          return false;
        }
      }
    }
    return true;
  }

}
