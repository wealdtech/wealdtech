/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.wealdtech.GenericWObject;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * The MangoPay API
 */
public interface MangoPayService
{
  @GET("{clientid}/clients")
  Call<GenericWObject> ping(@Header("Authorization") final String auth, @Path("clientid") final String clientId);

  @POST("{clientid}/users/natural")
  Call<GenericWObject> createUser(@Header("Authorization") final String auth,
                                  @Path("clientid") final String clientId,
                                  @Body GenericWObject body);

  @POST("{clientid}/wallets")
  Call<GenericWObject> createWallet(@Header("Authorization") final String auth,
                                    @Path("clientid") final String clientId,
                                    @Body GenericWObject body);

  @GET("{clientid}/cardregistrations/{registrationid}")
  Call<GenericWObject> obtainCardRegistration(@Header("Authorization") final String auth,
                                              @Path("clientid") final String clientId,
                                              @Path("registrationid") final String registrationidId);

  @POST("{clientid}/cardregistrations")
  Call<GenericWObject> createCardRegistration(@Header("Authorization") final String auth,
                                              @Path("clientid") final String clientId,
                                              @Body GenericWObject body);

  @PUT("{clientid}/cardregistrations/{registrationid}")
  Call<GenericWObject> updateCardRegistration(@Header("Authorization") final String auth,
                                              @Path("clientid") final String clientId,
                                              @Path("registrationid") final String registrationId,
                                              @Body GenericWObject body);
}
