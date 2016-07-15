package com.wealdtech.services.askgeo;

import com.wealdtech.WObject;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interact with the Ask Geo API
 */
public interface AskGeoService
{
  /**
   * Obtain list of autocomplete options
   * Points is a single point int he form lat,lng
   * @param accountId the ID of the account, for authentication/accounting purposes
   * @param apiKey the secret API key for the request
   * @param points the point for which to obtain the timezone
   * @return the result of the request, in AskGeo's format
   */
  @GET("/{accountid}/{apikey}/query.json?databases=TimeZone")
  WObject<WObject> timezone(@Path("accountid") final String accountId,
                            @Path("apikey") final String apiKey,
                            @Query("points") final String points);
}
