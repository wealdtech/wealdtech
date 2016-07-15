/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import com.wealdtech.GenericWObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Interact with the Google Account API
 */
public interface GoogleAccountsService
{
  /**
   * Obtain OAuth2 tokens
   *
   * @param grantType the type of the grant (usually "authorization_code")
   * @param clientId the client ID
   * @param clientSecret the client secret
   * @param redirectUri the Uri to redirect to after providing the token
   * @param code the code
   *
   * @return a generic response object
   */
  @FormUrlEncoded
  @POST("token")
  Call<GenericWObject> obtainToken(@Field(encoded = true, value = "grant_type") final String grantType,
                                   @Field(encoded = true, value = "client_id") final String clientId,
                                   @Field(encoded = true, value = "client_secret") final String clientSecret,
                                   @Field(encoded = true, value = "redirect_uri") final String redirectUri,
                                   @Field(encoded = true, value = "code") final String code);

  /**
   * Refresh OAuth2 access token
   *
   * @param grantType the type of the grant (usually "refresh_token")
   * @param clientId the client ID
   * @param clientSecret the client secret
   * @param refreshToken the user's refresh token
   *
   * @return a generic response object
   */
  @FormUrlEncoded
  @POST("token")
  Call<GenericWObject> refreshToken(@Field(encoded = true, value = "grant_type") final String grantType,
                                    @Field(encoded = true, value = "client_id") final String clientId,
                                    @Field(encoded = true, value = "client_secret") final String clientSecret,
                                    @Field(encoded = true, value = "refresh_token") final String refreshToken);
}
