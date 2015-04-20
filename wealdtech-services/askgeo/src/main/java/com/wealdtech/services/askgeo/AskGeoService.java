package com.wealdtech.services.askgeo;

import com.wealdtech.WObject;
import com.wealdtech.jackson.JDoc;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Interact with the Ask Geo API
 */
public interface AskGeoService
{
  /**
   * Obtain list of autocomplete options
   * Points is a single point int he form lat,lng
   */
  @GET("/{accountid}/{apikey}/query.json?databases=TimeZone")
  WObject<WObject> timezone(@Path("accountid") final String accountId,
                            @Path("apikey") final String apiKey,
                            @Query("points") final String points);
}
