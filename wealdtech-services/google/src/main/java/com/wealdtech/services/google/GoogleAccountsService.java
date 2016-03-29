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
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Interact with the Google Account API
 */
public interface GoogleAccountsService
{
  /**
   * Obtain OAuth2 tokens
   */
  @FormUrlEncoded
  @POST("/token")
  GenericWObject obtainToken(@Field(encodeName = false,encodeValue = false, value = "grant_type") final String grantType,
                             @Field(encodeName = false,encodeValue = false, value = "client_id") final String clientId,
                             @Field(encodeName = false,encodeValue = false, value = "client_secret") final String clientSecret,
                             @Field(encodeName = false,encodeValue = false, value = "redirect_uri") final String redirectUri,
                             @Field(encodeName = false,encodeValue = false, value = "code") final String code);

  /**
   * Refresh OAuth2 access token
   */
  @FormUrlEncoded
  @POST("/token")
  GenericWObject refreshToken(@Field(encodeName = false,encodeValue = false, value = "grant_type") final String grantType,
                              @Field(encodeName = false,encodeValue = false, value = "client_id") final String clientId,
                              @Field(encodeName = false,encodeValue = false, value = "client_secret") final String clientSecret,
                              @Field(encodeName = false,encodeValue = false, value = "refresh_token") final String refreshToken);
}
