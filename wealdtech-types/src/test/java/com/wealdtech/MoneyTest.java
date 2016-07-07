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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wealdtech.jackson.WealdMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class MoneyTest
{
  @Test
  public void simpleTest()
  {
    final Money money = Money.builder().currency(Currency.getInstance("EUR")).amount(new BigDecimal("12.34")).build();
    assertEquals(money.getCurrency(), Currency.getInstance("EUR"));
    assertEquals(money.getAmount(), new BigDecimal("12.34"));
  }

  @Test
  public void testSerialisation() throws JsonProcessingException
  {
    final Money money = Money.builder().currency(Currency.getInstance("EUR")).amount(new BigDecimal("12.34")).build();
    final String moneySer = WealdMapper.getServerMapper().writeValueAsString(money);
    assertEquals(moneySer, "{\"amount\":12.34,\"currency\":\"EUR\"}");
  }

  @Test
  public void testReserialisation() throws IOException
  {
    final Money money = Money.builder().currency(Currency.getInstance("EUR")).amount(new BigDecimal("12.34")).build();
    final String moneySer = WealdMapper.getServerMapper().writeValueAsString(money);
    final Money money2 = WealdMapper.getServerMapper().readValue(moneySer, Money.class);
    assertEquals(money2, money);
  }
}
