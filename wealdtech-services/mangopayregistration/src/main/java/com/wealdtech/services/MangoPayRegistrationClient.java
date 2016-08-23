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

import com.wealdtech.retrofit.RetrofitHelper;
import org.joda.time.YearMonth;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

/**
 * A specialised client to allow clients to register cards with MangoPay without needing to send the data to the application
 * server.
 */
public class MangoPayRegistrationClient
{
  private static volatile MangoPayRegistrationClient instance = null;

  private final MangoPayRegistrationService service;

  public static MangoPayRegistrationClient getInstance()
  {
    if (instance == null)
    {
      synchronized (MangoPayRegistrationClient.class)
      {
        if (instance == null)
        {
          instance = new MangoPayRegistrationClient();
        }
      }
    }
    return instance;
  }

  private MangoPayRegistrationClient()
  {
    service = RetrofitHelper.createRetrofit("http://www.wealdtech.com/", MangoPayRegistrationService.class);
  }

  public Observable<String> register(final String url, final String accessKey, final String preregistrationData, final String cardNumber, final YearMonth cardExpiry, final String cardCsc)
  {

    final Map<String, String> data = new HashMap<>();
    data.put("accessKeyRef", accessKey);
    data.put("data", preregistrationData);
    data.put("cardNumber", cardNumber);
    data.put("cardExpirationDate", cardExpiry.toString("MMYY"));
    data.put("cardCvx", cardCsc);

    return this.service.registerCard(url, data);
  }
}
