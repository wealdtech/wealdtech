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
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import com.wealdtech.weather.WeatherPoint;
import com.wealdtech.weather.WeatherPointType;
import com.wealdtech.weather.WeatherReport;
import com.wealdtech.weather.config.WeatherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

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
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).build();

    this.service = adapter.create(ForecastIoService.class);
  }

  public WeatherReport getPointInTimeReport(final float lat, final float lng, final Long timestamp)
  {
    final ForecastIoResponse response = service.forecastPoint(configuration.getApiKey(), lat, lng, timestamp);
    final WeatherReport.Builder<?> resultsB = WeatherReport.builder().type(WeatherPointType.HOUR);
    if (response.getCurrently().isPresent())
    {
      final ForecastIoReport report = response.getCurrently().get();
      resultsB.points(ImmutableList.of(buildPoint(report)));
    }
    return resultsB.build();
  }

  public WeatherReport getHourlyReport(final float lat, final float lng, final Long start, final Long end)
  {
    final WeatherReport.Builder<?> builder = WeatherReport.builder().type(WeatherPointType.HOUR);
    final ForecastIoResponse response = service.forecastHourly(configuration.getApiKey(), lat, lng, start);
    final ImmutableList<ForecastIoReport> reports = response.getHourlies().or(ImmutableList.<ForecastIoReport>of());
    final ImmutableList.Builder<WeatherPoint> pointsB = ImmutableList.builder();
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
