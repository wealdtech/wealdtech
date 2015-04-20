package com.wealdtech.services.google;

import com.wealdtech.WObject;
import com.wealdtech.jackson.JDoc;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interact with the Google PLAces API
 */
public interface GooglePlacesService
{
  /**
   * Obtain list of autocomplete options
   */
  @GET("/autocomplete/json?radius=50000")
  WObject<WObject> autocomplete(@Query("key") final String key,
                                @Query("input") final String input,
                                @Query("language") final String language,
                                @Query(value="location", encodeValue=false) final String location,
                                @Query("types") final String types);

  /**
   * Obtain full information on a place
   */
  @GET("/details/json")
  WObject<WObject> details(@Query("key") final String key,
                           @Query("placeid") final String placeId,
                           @Query("language") final String language);
}
