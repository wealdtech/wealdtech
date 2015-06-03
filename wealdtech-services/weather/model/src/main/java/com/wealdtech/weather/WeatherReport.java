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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A report about the weather.  It is made up of one or more weather points
 */
public class WeatherReport extends WObject<WeatherReport>
{
  private static final String TYPE = "type";
  private static final String POINTS = "points";

  @JsonCreator
  public WeatherReport(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Weather report failed validation: type required");
    checkState(exists(POINTS), "Weather report failed validation: points required");
  }

  @JsonIgnore
  public WeatherPointType getType() { return get(TYPE, WeatherPointType.class).get(); }

  @JsonIgnore
  private static final TypeReference<ImmutableList<WeatherPoint>> POINTS_TYPEREF = new TypeReference<ImmutableList<WeatherPoint>>(){};

  @JsonIgnore
  public ImmutableList<WeatherPoint> getPoints() { return get(POINTS, POINTS_TYPEREF).get(); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<WeatherReport, P>
  {
    public Builder(){ super(); }

    public Builder(final WeatherReport prior)
    {
      super(prior);
    }

    public P type(final WeatherPointType type)
    {
      data(TYPE, type);
      return self();
    }

    public P points(final ImmutableList<WeatherPoint> points)
    {
      data(POINTS, points);
      return self();
    }
    public WeatherReport build(){ return new WeatherReport(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final WeatherReport prior)
  {
    return new Builder(prior);
  }
}
