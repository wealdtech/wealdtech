package com.wealdtech.services.askgeo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;
import com.wealdtech.retrofit.RetrofitHelper;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Client for Ask Geo API
 */
public class AskGeoClient
{
  private static final Logger LOG = LoggerFactory.getLogger(AskGeoClient.class);

  private static final String ENDPOINT = "https://api.askgeo.com/v1";

  private static volatile AskGeoClient instance = null;

  public final AskGeoService service;

  public static AskGeoClient getInstance()
  {
    if (instance == null)
    {
      synchronized (AskGeoClient.class)
      {
        if (instance == null)
        {
          instance = new AskGeoClient();
        }
      }
    }
    return instance;
  }
  @Nullable
  public DateTimeZone timezone(final AskGeoConfiguration configuration, final Double lat, final Double lng)
  {
    final WObject<?> results = AskGeoClient.getInstance().service.timezone(configuration.getAccountId(),
                                                                                 configuration.getApiKey(), Double.toString(lat) + "," + Double.toString(lng));
    // Ensure that we have a valid response
    if (results == null || !results.exists("code") || results.get("code", Integer.class).get() != 0)
    {
      return null;
    }

    final Optional<ImmutableList<WObject<?>>> data = results.get("data", new TypeReference<ImmutableList<WObject<?>>>(){});
    if (!data.isPresent())
    {
      return null;
    }

    final Optional<WObject<?>> timezone = data.get().get(0).get("TimeZone");
    if (!timezone.isPresent())
    {
      return null;
    }

    return timezone.get().get("TimeZoneId", DateTimeZone.class).orNull();
  }

  private AskGeoClient()
  {
    this.service = RetrofitHelper.createRetrofit(ENDPOINT, AskGeoService.class);
  }
}
