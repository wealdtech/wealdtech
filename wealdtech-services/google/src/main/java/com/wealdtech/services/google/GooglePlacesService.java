package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interact with the Google Places API
 */
public interface GooglePlacesService
{
  /**
   * Obtain list of autocomplete options
   *
   * @param key the Google key
   * @param input the text input
   * @param language the language to use by preference when returning places
   * @param location any existing location information
   * @param types the types of places that should be returned as autocomplete candidates
   *
   * @return a list of autocomplete options
   */
  @GET("/autocomplete/json?radius=50000")
  GenericWObject autocomplete(@Query("key") final String key,
                              @Query("input") final String input,
                              @Query("language") final String language,
                              @Query(value = "location", encodeValue = false) final String location,
                              @Query("types") final String types);

  /**
   * Obtain full information on a place
   *
   * @param key the Google key
   * @param placeId the Id of the place returned by the autocomplete command
   * @param language the language to use by preference when returning places
   *
   * @return full place details
   */
  @GET("/details/json")
  GenericWObject details(@Query("key") final String key,
                         @Query("placeid") final String placeId,
                         @Query("language") final String language);
}
