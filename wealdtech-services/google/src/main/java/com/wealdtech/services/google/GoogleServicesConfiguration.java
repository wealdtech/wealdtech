package com.wealdtech.services.google;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.ServerError;
import com.wealdtech.configuration.Configuration;

/**
 */
public class GoogleServicesConfiguration implements Configuration
{
  private final String placesApiKey;

  public GoogleServicesConfiguration()
  {
    throw new ServerError("Not allowed to create google services configuration without information");
  }

  @JsonCreator
  public GoogleServicesConfiguration(@JsonProperty("placesapikey") final String placesApiKey)
  {
    this.placesApiKey = placesApiKey;
  }

  public String getPlacesApiKey()
  {
    return this.placesApiKey;
  }
}
