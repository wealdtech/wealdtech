/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.weather.services;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.wealdtech.weather.WeatherReport;
import com.wealdtech.weather.config.WeatherConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import javax.annotation.Nullable;

/**
 */
public class WeatherServiceForecastIoImpl implements WeatherService
{
  private ForecastIoClient client;

  @Inject
  public WeatherServiceForecastIoImpl(final WeatherConfiguration configuration)
  {
    client = new ForecastIoClient(configuration);
  }

  @Override
  @Nullable
  public WeatherReport getReport(final Float lat, final Float lng, final Range<DateTime> timeframe)
  {
    // We remove a second from the upper endpoint because our range is closedOpen
    final int hours = Hours.hoursBetween(timeframe.lowerEndpoint(), timeframe.upperEndpoint().minusSeconds(1)).getHours();
    if (hours < 1)
    {
      // Obtain point-in-time weather at start
      return client.getPointInTimeReport(lat, lng, timeframe.lowerEndpoint().getMillis() / 1000);
    }
    else if (hours <= 24)
    {
      // Obtain hourly weather over timeframe; move start back to the beginning of the hour and end to the nearest half-hour
      return client.getHourlyReport(lat, lng, timeframe.lowerEndpoint().withMinuteOfHour(0).getMillis() / 1000,
                                    timeframe.upperEndpoint().plusMinutes(30).withMinuteOfHour(0).getMillis() / 1000);
    }
    else
    {
      // Obtain daily weather over timeframe; reset to start-of-day boundaries
      // Reduce upper endpoint by 1 second because it is an open boundary
      return client.getDailyReport(lat, lng, timeframe.lowerEndpoint().withTimeAtStartOfDay().getMillis() / 1000,
                                   timeframe.upperEndpoint().withTimeAtStartOfDay().getMillis() / 1000);
    }
  }
}
