/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.gcm;

import com.wealdtech.WObject;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Interact with Google Cloud Messaging API
 */
public interface GcmService
{
  /**
   * Send a message
   * @param auth the authorization header for the request
   * @param body the request
   * @return the response
   */
  @POST("/send")
  Response sendMessage(@Header("Authorization") final String auth, @Body final WObject<?> body);
}
