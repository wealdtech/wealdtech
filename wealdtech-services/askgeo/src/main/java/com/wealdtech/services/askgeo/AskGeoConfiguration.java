package com.wealdtech.services.askgeo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.ServerError;
import com.wealdtech.configuration.Configuration;

/**
 */
public class AskGeoConfiguration implements Configuration
{
  private final String accountId;
  private final String apiKey;

  public AskGeoConfiguration()
  {
    throw new ServerError("Not allowed to create ask geo services configuration without information");
  }

  @JsonCreator
  public AskGeoConfiguration(@JsonProperty("accountid") final String accountId,
                             @JsonProperty("apikey") final String apiKey)
  {
    this.accountId = accountId;
    this.apiKey = apiKey;
  }

  public String getAccountId()
  {
    return this.accountId;
  }

  public String getApiKey()
  {
    return this.apiKey;
  }
}
