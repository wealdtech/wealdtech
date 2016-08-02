/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Details of the Stripe API
 */
public interface StripeService
{
  /**
   * Obtain OAuth2 tokens
   * @return a generic response object
   */
  @FormUrlEncoded
  @POST("token")
  Call<GenericWObject> obtainToken(@Field(encoded = true, value = "grant_type") final String grantType,
                                   @Field(encoded = true, value = "client_secret") final String clientSecret,
                                   @Field(encoded = true, value = "code") final String code);

  /**
   * Refresh OAuth2 access token
   * @return a generic response object
   */
  @FormUrlEncoded
  @POST("token")
  Call<GenericWObject> refreshToken(@Field(encoded = true, value = "grant_type") final String grantType,
                                    @Field(encoded = true, value = "client_secret") final String clientSecret,
                                    @Field(encoded = true, value = "refresh_token") final String refreshToken);
}
