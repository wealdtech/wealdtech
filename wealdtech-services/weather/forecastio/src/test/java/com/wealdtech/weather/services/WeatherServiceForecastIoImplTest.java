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
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import com.wealdtech.weather.WeatherPoint;
import com.wealdtech.weather.WeatherPointType;
import com.wealdtech.weather.WeatherReport;
import com.wealdtech.weather.config.WeatherConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 */
public class WeatherServiceForecastIoImplTest
{
  @Test
  public void testPoint()
  {
    final WeatherService service = new WeatherServiceForecastIoImpl(WeatherConfiguration.fromEnv("wealdtech_config_weather"));

    final WeatherReport report = service.getReport(50.861716f, -0.083873f,
                                                   Range.closedOpen(new DateTime(2015, 4, 1, 3, 0, 0, DateTimeZone.UTC),
                                                                    new DateTime(2015, 4, 1, 4, 0, 0, DateTimeZone.UTC)));
    assertNotNull(report, "Failed to obtain report");
    assertEquals(report.getType(), WeatherPointType.HOUR, "Obtained incorrect weather point type");
    final ImmutableList<WeatherPoint> points = report.getPoints();
    assertFalse(points.isEmpty(), "Failed to obtain weather point");
    assertEquals(points.size(), 1, "Failed to obtain correct number of weather points");
    final WeatherPoint point = points.get(0);
    assertEquals((long)point.getTimestamp(), new DateTime(2015, 4, 1, 3, 0, 0, DateTimeZone.UTC).getMillis(), "Incorrect timestamp");
    assertEquals(point.getTemperature().or(0f), 4.1f, 0.01f, "Incorrect temperature");
    assertEquals(point.getIcon().orNull(), "clear-night", "Incorrect icon");
  }

  @Test
  public void testMultipleHours()
  {
    final WeatherService service = new WeatherServiceForecastIoImpl(WeatherConfiguration.fromEnv("wealdtech_config_weather"));

    final WeatherReport report = service.getReport(50.861716f, -0.083873f,
                                                   Range.closedOpen(new DateTime(2015, 4, 1, 3, 0, 0, DateTimeZone.UTC),
                                                                    new DateTime(2015, 4, 1, 6, 0, 0, DateTimeZone.UTC)));
    assertNotNull(report, "Failed to obtain report");
    assertEquals(report.getType(), WeatherPointType.HOUR, "Obtained incorrect weather point type");
    final ImmutableList<WeatherPoint> points = report.getPoints();
    assertFalse(points.isEmpty(), "Failed to obtain weather point");
    assertEquals(points.size(), 3, "Failed to obtain correct number of weather points");
    final WeatherPoint point1 = points.get(0);
    assertEquals(point1.getTemperature().or(0f), 4.1f, 0.01f, "Incorrect temperature");
    assertEquals(point1.getIcon().orNull(), "clear-night", "Incorrect icon");
    final WeatherPoint point2 = points.get(1);
    assertEquals(point2.getTemperature().or(0f), 4.29f, 0.01f, "Incorrect temperature");
    assertEquals(point2.getIcon().orNull(), "clear-night", "Incorrect icon");
    final WeatherPoint point3 = points.get(2);
    assertEquals(point3.getTemperature().or(0f), 3.72f, 0.01f, "Incorrect temperature");
    assertEquals(point3.getIcon().orNull(), "clear-night", "Incorrect icon");
  }

  @Test
  public void testMultipleHoursOverMidnight()
  {
    final WeatherService service = new WeatherServiceForecastIoImpl(WeatherConfiguration.fromEnv("wealdtech_config_weather"));

    final WeatherReport report = service.getReport(50.861716f, -0.083873f,
                                                   Range.closedOpen(new DateTime(2015, 4, 1, 23, 0, 0, DateTimeZone.UTC),
                                                                    new DateTime(2015, 4, 2, 3, 0, 0, DateTimeZone.UTC)));
    assertNotNull(report, "Failed to obtain report");
    assertEquals(report.getType(), WeatherPointType.HOUR, "Obtained incorrect weather point type");
    final ImmutableList<WeatherPoint> points = report.getPoints();
    assertFalse(points.isEmpty(), "Failed to obtain weather point");
    assertEquals(points.size(), 4, "Failed to obtain correct number of weather points");
    final WeatherPoint point1 = points.get(0);
    assertEquals(point1.getTemperature().or(0f), 7.06f, 0.01f, "Incorrect temperature");
    assertEquals(point1.getIcon().orNull(), "clear-night", "Incorrect icon");
    final WeatherPoint point2 = points.get(1);
    assertEquals(point2.getTemperature().or(0f), 7.41f, 0.01f, "Incorrect temperature");
    assertEquals(point2.getIcon().orNull(), "clear-night", "Incorrect icon");
    final WeatherPoint point3 = points.get(2);
    assertEquals(point3.getTemperature().or(0f), 7.98f, 0.01f, "Incorrect temperature");
    assertEquals(point3.getIcon().orNull(), "clear-night", "Incorrect icon");
    final WeatherPoint point4 = points.get(3);
    assertEquals(point4.getTemperature().or(0f), 8.48f, 0.01f, "Incorrect temperature");
    assertEquals(point4.getIcon().orNull(), "clear-night", "Incorrect icon");
  }

  @Test(expectedExceptions = {DataError.Permission.class})
  public void testBadKey()
  {
    final WeatherService service = new WeatherServiceForecastIoImpl(new WeatherConfiguration("aaabbb"));

      service.getReport(50.861716f, -0.083873f, Range.closedOpen(new DateTime(2015, 4, 1, 23, 0, 0, DateTimeZone.UTC),
                                                                 new DateTime(2015, 4, 2, 3, 0, 0, DateTimeZone.UTC)));
  }
}
