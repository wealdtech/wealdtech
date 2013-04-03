package com.wealdtech.jersey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for cross-origin resource sharing in Jersey server
 */
public class CORSConfiguration implements Configuration
{
  // CORS configuration
  private String origin = "*";

  private boolean allowCredentials = true;

  private String allowedMethods = "GET, POST, PUT, DELETE, OPTIONS";

  @Inject
  public CORSConfiguration()
  {
  }

  @JsonCreator
  private CORSConfiguration(@JsonProperty("origin") final String origin,
                            @JsonProperty("allowcredentials") final Boolean allowCredentials,
                            @JsonProperty("allowedmethods") final String allowedMethods)
  {
    if (origin != null)
    {
      this.origin = origin;
    }

    if (allowCredentials != null)
    {
      this.allowCredentials = allowCredentials;
    }

    if (allowedMethods != null)
    {
      this.allowedMethods = allowedMethods;
    }
  }

  public String getOrigin()
  {
    return this.origin;
  }

  public boolean allowCredentials()
  {
    return this.allowCredentials;
  }

  public String getAllowedMethods()
  {
    return this.allowedMethods;
  }
}
