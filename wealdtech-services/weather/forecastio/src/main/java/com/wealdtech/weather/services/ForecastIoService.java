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

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 */
public interface ForecastIoService
{
  /**
   * Obtain a forecast for a particular location at a particular time
   *
   * @param key the api key
   * @param lat a latitude
   * @param lng a longitude
   * @param timestamp the timestamp in <b> seconds</b> since the epoch
   * @param cb the callback when the forecast has been obtained
   */
  @GET("/forecast/{key}/{lat},{lng},{timestamp}?units=uk&exclude=minutely,hourly,daily,alerts,flags")
  void forecast(@Path("key") final String key,
                @Path("lat") final float lat,
                @Path("lng") final float lng,
                @Path("timestamp") final long timestamp,
                Callback<ForecastIoResponse> cb);

  /**
   * Obtain a forecast for a particular location at a particular time
   *
   * @param key the api key
   * @param lat a latitude
   * @param lng a longitude
   * @param timestamp the timestamp in <b>seconds</b> since the epoch
   *
   * @return the forecast data
   */
  @GET("/forecast/{key}/{lat},{lng},{timestamp}?units=uk&exclude=minutely,hourly,daily,alerts,flags")
  ForecastIoResponse forecastPoint(@Path("key") final String key,
                                   @Path("lat") final float lat,
                                   @Path("lng") final float lng,
                                   @Path("timestamp") final long timestamp);


  /**
   * Obtain a forecast for a particular location at a particular time
   *
   * @param key the api key
   * @param lat a latitude
   * @param lng a longitude
   * @param timestamp the timestamp in <b>seconds</b> since the epoch
   *
   * @return the forecast data
   */
  @GET("/forecast/{key}/{lat},{lng},{timestamp}?units=si&exclude=currently,minutely,daily,alerts,flags")
  ForecastIoResponse forecastHourly(@Path("key") final String key,
                                    @Path("lat") final float lat,
                                    @Path("lng") final float lng,
                                    @Path("timestamp") final long timestamp);

  /**
   * Obtain a forecast for a particular location at a particular time
   *
   * @param key the api key
   * @param lat a latitude
   * @param lng a longitude
   * @param timestamp the timestamp in <b>seconds</b> since the epoch
   *
   * @return the forecast data
   */
  @GET("/forecast/{key}/{lat},{lng},{timestamp}?units=si&exclude=currently,minutely,hourly,alerts,flags")
  ForecastIoResponse forecastDaily(@Path("key") final String key,
                                   @Path("lat") final float lat,
                                   @Path("lng") final float lng,
                                   @Path("timestamp") final long timestamp);

  /**
   * Obtain a forecast for a particular location now
   *
   * @param key the api key
   * @param lat a latitude
   * @param lng a longitude
   *
   * @return the forecast data
   */
  @GET("/forecast/{key}/{lat},{lng}?units=si&exclude=currently,minutely,alerts,flags")
  ForecastIoResponse forecastNow(@Path("key") final String key,
                                 @Path("lat") final float lat,
                                 @Path("lng") final float lng);
}
