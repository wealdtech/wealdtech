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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

/**
 * A monetary value containing a currency and an amount
 */
public class Money extends WObject<Money> implements Comparable<Money>
{
  private static final String CURRENCY = "currency";
  private static final String AMOUNT = "amount";

  @JsonCreator
  public Money(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(CURRENCY)) { throw new DataError.Missing("Money failed validation: missing currency"); }
    if (!exists(AMOUNT)) { throw new DataError.Missing("Money failed validation: missing amount"); }
  }

  @JsonIgnore
  public Currency getCurrency() { return get(CURRENCY, Currency.class).get(); }

  @JsonIgnore
  public BigDecimal getAmount() { return get(AMOUNT, BigDecimal.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Money, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Money prior)
    {
      super(prior);
    }

    public P currency(final Currency currency)
    {
      data(CURRENCY, currency);
      return self();
    }

    public P amount(final BigDecimal amount)
    {
      data(AMOUNT, amount);
      return self();
    }


    public Money build()
    {
      return new Money(data);
    }
  }

  public static Builder<?> builder() { return new Builder(); }

  public static Builder<?> builder(final Money prior) { return new Builder(prior); }
}
