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

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static GoogleServicesConfiguration fromEnv(final String base)
  {
    return new GoogleServicesConfiguration(System.getenv(base + "_placesapikey"));
  }
}
