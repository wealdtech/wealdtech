/*
 * Copyright 2012 - 2017 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.cryptocurrency.services;

import com.wealdtech.GenericWObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface CoinMarketCapService
{
  @GET("ticker/")
  Call<List<GenericWObject>> obtainTickers(@Query("convert") String currency, @Query("limit") Integer limit);

  @GET("ticker/{key}/")
  Call<List<GenericWObject>> obtainTicker(@Path("key") String ticker, @Query("convert") String currency);
}
