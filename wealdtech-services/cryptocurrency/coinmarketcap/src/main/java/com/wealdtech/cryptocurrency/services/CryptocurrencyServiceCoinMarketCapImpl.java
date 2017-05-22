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

import com.google.inject.Inject;
import com.wealdtech.cryptocurrency.CryptocurrencyData;

import java.util.Currency;
import java.util.List;

public class CryptocurrencyServiceCoinMarketCapImpl implements CryptocurrencyService
{
  private CoinMarketCapClient client;

  @Inject
  public CryptocurrencyServiceCoinMarketCapImpl()

  {
    client = CoinMarketCapClient.getInstance();
  }

  @Override
  public List<CryptocurrencyData> getCurrencies(final Currency currency, final Integer limit)
  {
    return client.obtainTickers(currency, limit);
  }

  @Override
  public CryptocurrencyData getCurrency(final Currency currency, final String symbol)
  {
    return null;
  }
}
