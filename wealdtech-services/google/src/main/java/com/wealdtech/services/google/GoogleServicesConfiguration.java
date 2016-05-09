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
  private final String timezonesApiKey;

  public GoogleServicesConfiguration()
  {
    throw new ServerError("Not allowed to create google services configuration without information");
  }

  @JsonCreator
  public GoogleServicesConfiguration(@JsonProperty("placesapikey") final String placesApiKey,
                                     @JsonProperty("directionsapikey") final String directionsApiKey,
                                     @JsonProperty("timezonesapikey") final String timezonesApiKey)
  {
    this.placesApiKey = placesApiKey;
    this.directionsApiKey = directionsApiKey;
    this.timezonesApiKey = timezonesApiKey;
  }

  public String getPlacesApiKey()
  {
    return this.placesApiKey;
  }

  public String getDirectionsApiKey()
  {
    return this.directionsApiKey;
  }

  public String getTimezonesApiKey()
  {
    return this.timezonesApiKey;
  }

  /**
   * @param base the base string to use as the prefix for obtaining environmental variables
   * @return a configuration from the environment
   */
  public static GoogleServicesConfiguration fromEnv(final String base)
  {
    return new GoogleServicesConfiguration(System.getenv(base + "_placesapikey"),
                                           System.getenv(base + "_directionsapikey"),
                                           System.getenv(base + "_timezonesapikey"));
  }
}
