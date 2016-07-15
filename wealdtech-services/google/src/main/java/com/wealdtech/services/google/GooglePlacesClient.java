package com.wealdtech.services.google;

import com.wealdtech.retrofit.RetrofitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for Google Places API
 */
public class GooglePlacesClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GooglePlacesClient.class);

  private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place/";

  private static volatile GooglePlacesClient instance = null;

  public final GooglePlacesService service;

  public static GooglePlacesClient getInstance()
  {
    if (instance == null)
    {
      synchronized (GooglePlacesClient.class)
      {
        if (instance == null)
        {
          instance = new GooglePlacesClient();
        }
        }
    }
    return instance;
  }

  private GooglePlacesClient()
  {
    this.service = RetrofitHelper.createRetrofit(ENDPOINT, GooglePlacesService.class);
  }
}
