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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.wealdtech.GenericWObject;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Client for Google Places API
 */
public class GoogleDirectionsClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GoogleDirectionsClient.class);

  private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/directions";

  private final GoogleServicesConfiguration configuration;

  public final GoogleDirectionsService service;

  private static final TypeReference<List<GenericWObject>> LIST = new TypeReference<List<GenericWObject>>() {};

  @Inject
  public GoogleDirectionsClient(final GoogleServicesConfiguration configuration)
  {
    this.configuration = configuration;

    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).build();

    this.service = adapter.create(GoogleDirectionsService.class);
  }

  /**
   * @param startLat the start latitude in microdegrees
   * @param startLng the start longitude in microdegrees
   * @param endLat the end latitude in microdegrees
   * @param endLng the end longitude in microdegrees
   * @param mode the mode - should be "driving"
   * @param startTimestamp the timestamp of the start of the journey
   *
   * @return the expected number of seconds for the journey
   */
  @Nullable
  public Integer getDurationOfJourney(final Long startLat,
                                      final Long startLng,
                                      final Long endLat,
                                      final Long endLng,
                                      final String mode,
                                      final Long startTimestamp)
  {
    final String start = Double.toString(startLat / 1000000.0) + "," + Double.toString(startLng / 1000000.0);
    final String end = Double.toString(endLat / 1000000.0) + "," + Double.toString(endLng / 1000000.0);
    GenericWObject object = service.directions(configuration.getDirectionsApiKey(), startTimestamp, start, end, mode);
    final Optional<List<GenericWObject>> routes = object.get("routes", LIST);
    if (routes.isPresent() && !routes.get().isEmpty())
    {
      final Optional<List<GenericWObject>> legs = routes.get().get(0).get("legs", LIST);
      if (legs.isPresent() && !legs.get().isEmpty())
      {
        final Optional<GenericWObject> duration = legs.get().get(0).get("duration", GenericWObject.class);
        if (duration.isPresent())
        {
          final Optional<Integer> value = duration.get().get("value", Integer.class);
          if (value.isPresent())
          {
            return value.get();
          }
        }
      }
    }
    return null;
  }
}
