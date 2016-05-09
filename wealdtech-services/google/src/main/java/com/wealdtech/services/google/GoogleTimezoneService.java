/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interact with the Google Timezone API
 */
public interface GoogleTimezoneService
{
  /**
   * @param key the Google key
   * @param location the location as a lat,lng pair
   * @param timestamp the timestamp for which to obtain the information (for DST info)
   *
   * @return a timezone for the given lat/lng
   */
  @GET("/json")
  GenericWObject timezone(@Query("key") final String key,
                          @Query("location") final String location,
                          @Query("timestamp") final long timestamp);
}
