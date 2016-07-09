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
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interact with the Google ID Token API
 */
public interface GoogleIdTokenService
{
  /**
   * Obtain ID information given a token
   * @param idToken the token
   *
   * @return a generic response object
   */
  @GET("/tokeninfo")
  GenericWObject obtainInfo(@Query("id_token") final String idToken);
}
