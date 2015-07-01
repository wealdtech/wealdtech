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
  private final String directionsApiKey;

  public GoogleServicesConfiguration()
  {
    throw new ServerError("Not allowed to create google services configuration without information");
  }

  @JsonCreator
  public GoogleServicesConfiguration(@JsonProperty("placesapikey") final String placesApiKey,
                                     @JsonProperty("directionsapikey") final String directionsApiKey)
  {
    this.placesApiKey = placesApiKey;
    this.directionsApiKey = directionsApiKey;
  }

  public String getPlacesApiKey()
  {
    return this.placesApiKey;
  }

  public String getDirectionsApiKey()
  {
    return this.directionsApiKey;
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static GoogleServicesConfiguration fromEnv(final String base)
  {
    return new GoogleServicesConfiguration(System.getenv(base + "_placesapikey"), System.getenv(base + "_directionsapikey"));
  }
}
