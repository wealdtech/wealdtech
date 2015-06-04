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
import com.wealdtech.ClientError;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;
import com.wealdtech.weather.WeatherReport;
import com.wealdtech.weather.config.WeatherConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import retrofit.RetrofitError;

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
  public WeatherReport getReport(final Float lat, final Float lng, final Range<DateTime> timeframe)
  {
    try
    {
      final int hours = Hours.hoursBetween(timeframe.lowerEndpoint(), timeframe.upperEndpoint()).getHours();
      if (hours <= 1)
      {
        // Obtain point-in-time weather at start
        return client.getPointInTimeReport(lat, lng, timeframe.lowerEndpoint().getMillis() / 1000);
      }
      else if (hours <= 24)
      {
        // Obtain hourly weather over timeframe
        return client.getHourlyReport(lat, lng, timeframe.lowerEndpoint().getMillis() / 1000,
                                      timeframe.upperEndpoint().getMillis() / 1000);
      }
      else
      {
        // Obtain daily weather over timeframe (max 7 days)
        throw new ServerError("Not implemented");
      }
    }
    catch (final RetrofitError re)
    {
      switch (re.getKind())
      {
        case NETWORK:
          // This happens when a network connection fails
          throw new ClientError("Failed to reach server", re);
        case CONVERSION:
          // This happens when a conversion from JSON to Java fails
          throw new ServerError("Unexpected response data", re);
        case HTTP:
          // This happens when the HTTP request returned an unsuitable response
          final int status = re.getResponse().getStatus();
          if (status == 401)
          {
            throw new DataError.Authentication("Authentication failed");
          }
          else if (status == 403)
          {
            throw new DataError.Permission("Not authorized");
          }
          else if (status == 404)
          {
            throw new DataError.Missing("Unknown endpoint");
          }
          else
          {
            throw new DataError.Bad("Failure code " + status);
          }
        default:
          throw new ServerError(re);
      }
    }
  }
}
