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

import com.google.common.base.Optional;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import javax.annotation.Nullable;

/**
 * Client for Google Timezone API
 */
public class GoogleTimezoneClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GoogleTimezoneClient.class);

  private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/timezone/";

  private static volatile GoogleTimezoneClient instance = null;

  public final GoogleTimezoneService service;

  public static GoogleTimezoneClient getInstance()
  {
    if (instance == null)
    {
      synchronized (GoogleTimezoneClient.class)
      {
        if (instance == null)
        {
          instance = new GoogleTimezoneClient();
        }
        }
    }
    return instance;
  }

  private GoogleTimezoneClient()
  {
    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT)
                                                         .setConverter(converter)
                                                         .setLogLevel(RestAdapter.LogLevel.FULL)
                                                         .build();
    this.service = adapter.create(GoogleTimezoneService.class);
  }

  @Nullable
  public DateTimeZone timezone(final GoogleServicesConfiguration configuration, final Double lat, final Double lng)
  {
    final WObject<?> results = service.timezone(configuration.getTimezonesApiKey(),
                                                Double.toString(lat) + "," + Double.toString(lng), DateTime.now().getMillis() / 1000);
    // Ensure that we have a valid response
    if (results == null || !results.exists("status") || !results.get("status", String.class).get().equals("OK"))
    {
      return null;
    }

    final Optional<String> timezoneString = results.get("timeZoneId", String.class);
    if (!timezoneString.isPresent())
    {
      return null;
    }

    return DateTimeZone.forID(timezoneString.get());
  }
}
