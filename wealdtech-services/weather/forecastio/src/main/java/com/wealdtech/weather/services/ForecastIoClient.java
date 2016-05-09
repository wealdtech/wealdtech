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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.DataError;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import com.wealdtech.weather.WeatherPoint;
import com.wealdtech.weather.WeatherPointType;
import com.wealdtech.weather.WeatherReport;
import com.wealdtech.weather.config.WeatherConfiguration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;

import javax.annotation.Nullable;

/**
 */
public class ForecastIoClient
{
  private static final Logger LOG = LoggerFactory.getLogger(ForecastIoClient.class);

  private static final String ENDPOINT = "https://api.forecast.io";

  private final WeatherConfiguration configuration;

  public final ForecastIoService service;

  @Inject
  public ForecastIoClient(final WeatherConfiguration configuration)
  {
    this.configuration = configuration;

    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).setLogLevel(RestAdapter.LogLevel.FULL).build();

    this.service = adapter.create(ForecastIoService.class);
  }

  @Nullable
  public WeatherReport getPointInTimeReport(final float lat, final float lng, final Long timestamp)
  {
    final ForecastIoResponse response;
    try
    {
      response = service.forecastPoint(configuration.getApiKey(), lat, lng, timestamp);
    }
    catch (final RetrofitError e)
    {
      if (e.getResponse().getStatus() == 400)
      {
        // Due to a bad key
        throw new DataError.Permission("Invalid key");
      }
      else
      {
        throw new DataError.Bad("An error occurred: ", e);
      }
    }
    catch (final Exception e)
    {
      LOG.error("Failed to obtain point in time report: ", e);
      return null;
    }
    final WeatherReport.Builder<?> resultsB = WeatherReport.builder().type(WeatherPointType.HOUR);
    if (response.getCurrently().isPresent())
    {
      final ForecastIoReport report = response.getCurrently().get();
      resultsB.points(ImmutableList.of(buildPoint(report)));
    }
    return resultsB.build();
  }

  @Nullable
  public WeatherReport getHourlyReport(final float lat, final float lng, final Long start, final Long end)
  {
    final WeatherReport.Builder<?> builder = WeatherReport.builder().type(WeatherPointType.HOUR);
    final ForecastIoResponse response;
    try
    {
      response = service.forecastHourly(configuration.getApiKey(), lat, lng, start);
    }
    catch (final RetrofitError e)
    {
      if (e.getResponse().getStatus() == 400)
      {
        // Due to a bad key
        throw new DataError.Permission("Invalid key");
      }
      else
      {
        throw new DataError.Bad("An error occurred: ", e);
      }
    }
    catch (final Exception e)
    {
      LOG.error("Failed to obtain hourly report: ", e);
      return null;
    }
    final ImmutableList<ForecastIoReport> reports = response.getHourlies().or(ImmutableList.<ForecastIoReport>of());
    final ImmutableList.Builder<WeatherPoint> pointsB = ImmutableList.builder();
    for (final ForecastIoReport report : reports)
    {
      final Long timestamp = report.getTimestamp().or(0l);
      if (timestamp < start)
      {
        continue;
      }
      // Note that usually this would be a >=, but in this case we do want the weather report at the end time as well
      if (timestamp > end)
      {
        break;
      }
      pointsB.add(buildPoint(report));
    }
    builder.points(pointsB.build());
    return builder.build();
  }

  @Nullable
  public WeatherReport getDailyReport(final float lat, final float lng, final Long start, final Long end)
  {
    final WeatherReport.Builder<?> builder = WeatherReport.builder().type(WeatherPointType.DAY);
    final ImmutableList.Builder<WeatherPoint> pointsB = ImmutableList.builder();

    // Forecast.io returns daily information one day at a time unless for the current date in which case it will attempt to return
    // seven days.
    final long now = new DateTime().getMillis() / 1000;
    final long oneWeeksTime = new DateTime().plusWeeks(1).getMillis() / 1000;
    if (start > now && start < oneWeeksTime && end > now && end < oneWeeksTime)
    {
      // We can use the current report
      final ForecastIoResponse response;
      try
      {
        response = service.forecastNow(configuration.getApiKey(), lat, lng);
      }
      catch (final RetrofitError e)
      {
        if (e.getResponse().getStatus() == 400)
        {
          // Due to a bad key
          throw new DataError.Permission("Invalid key");
        }
        else
        {
          throw new DataError.Bad("An error occurred: ", e);
        }
      }
      catch (final Exception e)
      {
        LOG.error("Failed to obtain immediate report: ", e);
        return null;
      }
      final ImmutableList<ForecastIoReport> reports = response.getDailies().or(ImmutableList.<ForecastIoReport>of());
      for (final ForecastIoReport report : reports)
      {
        final Long timestamp = report.getTimestamp().or(0l);
        if (timestamp < start)
        {
          continue;
        }
        if (timestamp >= end)
        {
          break;
        }
        pointsB.add(buildPoint(report));
      }
    }
    else
    {
      // Need to obtain info for each individual day (up to a maximum of 7)
      DateTime cur = new DateTime(start * 1000);
      int obtained = 0;
      while (cur.getMillis() / 1000 < end && obtained++ < 7)
      {
        final ForecastIoResponse response;
        try
        {
          response = service.forecastDaily(configuration.getApiKey(), lat, lng, cur.getMillis() / 1000);
        }
        catch (final RetrofitError e)
        {
          if (e.getResponse().getStatus() == 400)
          {
            // Due to a bad key
            throw new DataError.Permission("Invalid key");
          }
          else
          {
            throw new DataError.Bad("An error occurred: ", e);
          }
        }
        catch (final Exception e)
        {
          LOG.error("Failed to obtain daily report: ", e);
          return null;
        }

        final ImmutableList<ForecastIoReport> reports = response.getDailies().or(ImmutableList.<ForecastIoReport>of());
        if (!reports.isEmpty())
        {
          pointsB.add(buildPoint(reports.get(0)));
        }
        cur = cur.plusDays(1);
      }
    }
    builder.points(pointsB.build());
    return builder.build();
  }

  private WeatherPoint buildPoint(final ForecastIoReport report)
  {
    final WeatherPoint.Builder<?> builder = WeatherPoint.builder();

    if (report.getTimestamp().isPresent())
    {
      builder.timestamp(report.getTimestamp().get() * 1000l);
    }

    if (report.getMinTemperature().isPresent())
    {
      builder.minTemperature(report.getMinTemperature().get());
    }

    if (report.getMaxTemperature().isPresent())
    {
      builder.maxTemperature(report.getMaxTemperature().get());
    }

    if (report.getTemperature().isPresent())
    {
      builder.temperature(report.getTemperature().get());
    }

    if (report.getTemperature().isPresent())
    {
      builder.temperature(report.getTemperature().get());
    }

    if (report.getIcon().isPresent())
    {
      builder.icon(report.getIcon().get());
    }
    return builder.build();
  }
}
