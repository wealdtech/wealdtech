/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A set of information regarding the weather
 */
public class WeatherPoint extends WObject<WeatherPoint>
{
  private static final String TIMESTAMP = "timestamp";
  private static final String MIN_TEMP = "mintemp";
  private static final String MAX_TEMP = "maxtemp";
  private static final String TEMPERATURE = "temperature";
  private static final String ICON = "icon";

  @JsonCreator
  public WeatherPoint(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(TIMESTAMP), "Weather point failed validation: timestamp required");
    checkState(exists(ICON), "Weather point failed validation: icon required");
  }

  @JsonIgnore
  public Long getTimestamp() { return get(TIMESTAMP, Long.class).get(); }

  @JsonIgnore
  public Optional<Float> getMinTemp() { return get(MIN_TEMP, Float.class); }

  @JsonIgnore
  public Optional<Float> getMaxTemp() { return get(MAX_TEMP, Float.class); }

  @JsonIgnore
  public Optional<Float> getTemperature() { return get(TEMPERATURE, Float.class); }

  @JsonIgnore
  public Optional<String> getIcon() { return get(ICON, String.class); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<WeatherPoint, P>
  {
    public Builder(){ super(); }

    public Builder(final WeatherPoint prior)
    {
      super(prior);
    }

    public P timestamp(final Long timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P minTemp(final Float minTemp)
    {
      data(MIN_TEMP, minTemp);
      return self();
    }

    public P maxTemp(final Float maxTemp)
    {
      data(MAX_TEMP, maxTemp);
      return self();
    }

    public P temperature(final Float temperature)
    {
      data(TEMPERATURE, temperature);
      return self();
    }

    public P icon(final String icon)
    {
      data(ICON, icon);
      return self();
    }

    public WeatherPoint build(){ return new WeatherPoint(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final WeatherPoint prior)
  {
    return new Builder(prior);
  }
}
