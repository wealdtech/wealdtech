/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interact with the Google Directions API
 */
public interface GoogleDirectionsService
{
  /**
   * Obtain directions
   *
   * @param key the Google key
   * @param departureTime the time at which the user wishes to depart
   * @param origin the place from which the user wishes to leave
   * @param destination the place to which the user wishes to travel
   * @param mode the mode of transport
   *
   * @return directions
   */
  @GET("json")
  Call<GenericWObject> directions(@Query("key") final String key,
                                  @Query("departure_time") final Long departureTime,
                                  @Query("origin") final String origin,
                                  @Query("destination") final String destination,
                                  @Query("mode") final String mode);
}
