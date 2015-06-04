/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Money represents a value tied to a currency
 */
public class Money
{
  private final Currency currency;
  private final BigDecimal amount;

  public Money(final Currency currency, final BigDecimal amount)
  {
    this.currency = currency;
    this.amount = amount;
  }

  public Money(final String input)
  {
    this.currency = parseCurrency(input);
    this.amount = parseAmount(input);
  }

  /**
   * Find the currency given a value as input
   * @return a suitable currency
   */
  private static Currency parseCurrency(final String input)
  {
    throw new ServerError("To implement");
//    throw new DataError.Bad("Missing currency symbol");
  }

  private static BigDecimal parseAmount(final String input)
  {
    throw new ServerError("To implement");
  }

  public Currency getCurrency()
  {
    return currency;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }
}
