/*
 * Copyright 2012 - 2017 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.cryptocurrency;

import com.wealdtech.cryptocurrency.services.CryptocurrencyService;
import com.wealdtech.cryptocurrency.services.CryptocurrencyServiceCoinMarketCapImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Currency;
import java.util.List;

public class CryptocurrencyServiceCoinMarketCapImplTest
{
  @Test
  public void testObtainTicker1()
  {
    final CryptocurrencyService service = new CryptocurrencyServiceCoinMarketCapImpl();

    final CryptocurrencyData data = service.getTicker("ethereum", "EUR");

    Assert.assertEquals("ETH", data.getSymbol());

    Assert.assertTrue(data.getName().isPresent());
    Assert.assertEquals("Ethereum", data.getName().get());

    Assert.assertTrue(data.getPrice().isPresent());
    Assert.assertEquals("EUR", data.getPrice().get().getCurrency());
  }

  @Test
  public void testObtainTickers1()
  {
    final CryptocurrencyService service = new CryptocurrencyServiceCoinMarketCapImpl();

    final List<CryptocurrencyData> data = service.getTickers("EUR", 10);

    for (CryptocurrencyData datum : data)
    {
      Assert.assertTrue(datum.getName().isPresent());
      Assert.assertTrue(datum.getPrice().isPresent());
    }
  }
}
