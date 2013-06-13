package com.wealdtech.jersey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for Jersey server
 */
public class JerseyServerConfiguration implements Configuration
{
  // CORS configuration
  private CORSConfiguration corsConfiguration = new CORSConfiguration();

  @Inject
  public JerseyServerConfiguration()
  {

  }

  @JsonCreator
  private JerseyServerConfiguration(@JsonProperty("cors") final CORSConfiguration corsConfiguration)
  {
    this.corsConfiguration = Objects.firstNonNull(corsConfiguration, this.corsConfiguration);
  }

  public CORSConfiguration getCORSConfiguration()
  {
    return this.corsConfiguration;
  }
}
