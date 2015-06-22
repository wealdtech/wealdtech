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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.wealdtech.GenericWObject;
import com.wealdtech.WObject;
import org.joda.time.DateTimeZone;

import java.util.Map;

/**
 */
public class ForecastIoResponse extends WObject<ForecastIoResponse>
{
  private static final String LATITUDE = "latitude";
  private static final String LONGITUDE = "longitude";
  private static final String TIMEZONE = "timezone";
  private static final String CURRENTLY = "currently";
  private static final String HOURLY = "hourly";
  private static final String DAILY = "daily";

  @JsonCreator
  public ForecastIoResponse(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<Float> getLatitude() { return get(LATITUDE, Float.class); }

  @JsonIgnore
  public Optional<Float> getLongitude() { return get(LONGITUDE, Float.class); }

  @JsonIgnore
  public Optional<DateTimeZone> getTimezone() { return get(TIMEZONE, DateTimeZone.class); }

  @JsonIgnore
  public Optional<ForecastIoReport> getCurrently() { return get(CURRENTLY, ForecastIoReport.class); }

  @JsonIgnore
  private static final TypeReference<ImmutableList<ForecastIoReport>> HOURLIES_TYPEREF =
      new TypeReference<ImmutableList<ForecastIoReport>>() {};

  @JsonIgnore
  public Optional<ImmutableList<ForecastIoReport>> getHourlies()
  {
    final Optional<GenericWObject> obj = get(HOURLY, GenericWObject.class);
    if (obj.isPresent())
    {
      return obj.get().get("data", HOURLIES_TYPEREF);
    }
    else
    {
      return Optional.absent();
    }
  }

  @JsonIgnore
  private static final TypeReference<ImmutableList<ForecastIoReport>> DAILIES_TYPEREF =
      new TypeReference<ImmutableList<ForecastIoReport>>() {};

  @JsonIgnore
  public Optional<ImmutableList<ForecastIoReport>> getDailies()
  {
    final Optional<GenericWObject> obj = get(DAILY, GenericWObject.class);
    if (obj.isPresent())
    {
      return obj.get().get("data", DAILIES_TYPEREF);
    }
    else
    {
      return Optional.absent();
    }
  }
}
