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
import com.google.common.base.Optional;
import com.wealdtech.WObject;

import java.util.Map;

/**
 */
public class ForecastIoReport extends WObject<ForecastIoReport>
{
  private static final String TIMESTAMP = "time";
  private static final String TEMPERATURE = "temperature";
  private static final String MAX_TEMPERATURE = "temperatureMax";
  private static final String MIN_TEMPERATURE = "temperatureMin";
  private static final String APPARENT_TEMPERATURE = "apparentTemperature";
  private static final String ICON = "icon";

  @JsonCreator
  public ForecastIoReport(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<Long> getTimestamp() { return get(TIMESTAMP, Long.class); }

  @JsonIgnore
  public Optional<Float> getMaxTemperature() { return get(MAX_TEMPERATURE, Float.class); }

  @JsonIgnore
  public Optional<Float> getMinTemperature() { return get(MIN_TEMPERATURE, Float.class); }

  @JsonIgnore
  public Optional<Float> getTemperature() { return get(TEMPERATURE, Float.class); }

  @JsonIgnore
  public Optional<Float> getApparentTemperature() { return get(APPARENT_TEMPERATURE, Float.class); }

  @JsonIgnore
  public Optional<String> getIcon() { return get(ICON, String.class); }
}
