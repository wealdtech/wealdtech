package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
  @GET("autocomplete/json?radius=50000")
  Call<GenericWObject> autocomplete(@Query("key") final String key,
                                    @Query("input") final String input,
                                    @Query("language") final String language,
                                    @Query(value = "location", encoded = true) final String location,
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
  @GET("details/json")
  Call<GenericWObject> details(@Query("key") final String key,
                               @Query("placeid") final String placeId,
                               @Query("language") final String language);
}
