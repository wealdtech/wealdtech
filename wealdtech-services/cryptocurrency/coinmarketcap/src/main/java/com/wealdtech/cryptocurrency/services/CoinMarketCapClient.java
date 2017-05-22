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
import com.wealdtech.Money;
import com.wealdtech.cryptocurrency.CryptocurrencyData;
import com.wealdtech.retrofit.RetrofitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class CoinMarketCapClient
{
  private static final Logger LOG = LoggerFactory.getLogger(CoinMarketCapClient.class);

  private static volatile CoinMarketCapClient instance = null;

  private final CoinMarketCapService service;

  public static CoinMarketCapClient getInstance()
  {
    if (instance == null)
    {
      synchronized (CoinMarketCapClient.class)
      {
        if (instance == null)
        {
          instance = new CoinMarketCapClient();
        }
      }
    }
    return instance;
  }

  private CoinMarketCapClient()
  {
    this.service = RetrofitHelper.createRetrofit("https://api.coinmarketcap.com/v1/", CoinMarketCapService.class);
  }

  public List<CryptocurrencyData> obtainTickers(final Currency currency, final Integer limit)
  {
    List<CryptocurrencyData> data = new ArrayList<>();
    try
    {
      final Response<List<GenericWObject>> response = service.obtainTickers(currency.getCurrencyCode(), limit).execute();
      if (!response.isSuccessful())
      {
        LOG.error("Failed to obtain tickers: {} ", response.errorBody().string());
      }
      else
      {
        for (GenericWObject item : response.body())
        {
          final CryptocurrencyData datum = convertItem(currency, item);
        }
      }
    }
    catch (IOException ioe)
    {
      // Ignored
    }
    return data;
  }

  public CryptocurrencyData obtainTicker(final String ticker, final Currency currency)
  {
    CryptocurrencyData result = null;
    try
    {
      final Response<List<GenericWObject>> response = service.obtainTicker(ticker, currency.getCurrencyCode()).execute();
      if (!response.isSuccessful())
      {
        LOG.error("Failed to obtain ticker: {} ", response.errorBody().string());
      }
      else
      {
        result = convertItem(currency, response.body().iterator().next());
      }
    }
    catch (IOException ioe)
    {
      // Ignored
    }
    return result;
  }

  public static CryptocurrencyData convertItem(final Currency currency, final GenericWObject item)
  {
    final CryptocurrencyData.Builder<?> builder =
        CryptocurrencyData.builder().symbol(item.get("symbol", String.class).orNull())
                          .key(item.get("id", String.class).get());
    if (item.exists("name"))
    {
      builder.name(item.get("name", String.class).get());
    }
    if (item.exists("timestamp"))
    {
      builder.timestamp(item.get("timestamp", Long.class).get());
    }

    if (item.exists("percent_change_1h"))
    {
      builder.change1H(new BigDecimal(item.get("percent_change_1h", String.class).get()));
    }

    if (item.exists("percent_change_24h"))
    {
      builder.change24H(new BigDecimal(item.get("percent_change_24h", String.class).get()));
    }

    if (item.exists("percent_change_7d"))
    {
      builder.change7D(new BigDecimal(item.get("percent_change_7d", String.class).get()));
    }

    if (item.exists("available_supply"))
    {
      builder.availableSupply(new BigDecimal(item.get("available_supply", String.class).get()));
    }

    if (item.exists("total_supply"))
    {
      builder.totalSupply(new BigDecimal(item.get("total_supply", String.class).get()));
    }


    if (item.exists("last_updated"))
    {
      builder.timestamp(item.get("last_updated", Long.class).get());
    }


    // Remaining items are currency-dependent
    final String currencyCode = currency.getCurrencyCode().toLowerCase();

    if (item.exists("price_" + currencyCode))
    {
      builder.price(Money.builder()
                         .amount(new BigDecimal(item.get("price_" + currencyCode, String.class).get()))
                         .currency(currency)
                         .build());
    }

    if (item.exists("24h_volume_" + currencyCode))
    {
      builder.volume24H(Money.builder()
                             .amount(new BigDecimal(item.get("24h_volume_" + currencyCode, String.class).get()))
                             .currency(currency)
                             .build());
    }

    if (item.exists("market_cap_" + currencyCode))
    {
      builder.marketCap(Money.builder()
                             .amount(new BigDecimal(item.get("market_cap_" + currencyCode, String.class).get()))
                             .currency(currency)
                             .build());
    }

    return builder.build();
  }
}
