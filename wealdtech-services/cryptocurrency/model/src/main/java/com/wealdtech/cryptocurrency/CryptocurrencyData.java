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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.wealdtech.WObject;
import com.wealdtech.Money;
import sun.security.x509.AVA;

import java.math.BigDecimal;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A set of information regarding a single cryptocurrency at a point in time
 */
public class CryptocurrencyData extends WObject<CryptocurrencyData>
{
  private static final String KEY = "key";
  private static final String SYMBOL = "symbol";
  private static final String NAME = "name";
  private static final String PRICE = "price";
  private static final String MARKET_CAP = "marketcap";
  private static final String VOLUME_24H = "volume24h";
  private static final String AVAILABLE_SUPPLY = "availablesupply";
  private static final String TOTAL_SUPPLY = "totalsupply";
  private static final String CHANGE_1H = "change1h";
  private static final String CHANGE_24H = "change24h";
  private static final String CHANGE_7D = "change7d";
  private static final String TIMESTAMP = "timestamp";

  @JsonCreator
  public CryptocurrencyData(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(SYMBOL), "Cryptocurrency data failed validation: timestamp required");
  }

  @JsonIgnore
  public String getKey() { return get(KEY, String.class).get(); }

  @JsonIgnore
  public String getSymbol() { return get(SYMBOL, String.class).get(); }

  @JsonIgnore
  public Optional<String> getName() { return get(NAME, String.class); }

  @JsonIgnore
  public Optional<Money> getPrice() { return get(PRICE, Money.class); }

  @JsonIgnore
  public Optional<Money> getMarketCap() { return get(MARKET_CAP, Money.class); }

  @JsonIgnore
  public Optional<Money> getVolume24H() { return get(VOLUME_24H, Money.class); }

  @JsonIgnore
  public Optional<BigDecimal> getAvailableSupply() { return get(AVAILABLE_SUPPLY, BigDecimal.class); }

  @JsonIgnore
  public Optional<BigDecimal> getTotalSupply() { return get(TOTAL_SUPPLY, BigDecimal.class); }

  @JsonIgnore
  public Optional<BigDecimal> getChange1H() { return get(CHANGE_1H, BigDecimal.class); }

  @JsonIgnore
  public Optional<BigDecimal> getChange24H() { return get(CHANGE_24H, BigDecimal.class); }

  @JsonIgnore
  public Optional<BigDecimal> getChange7D() { return get(CHANGE_7D, BigDecimal.class); }

  @JsonIgnore
  public Optional<Long> getTimestamp() { return get(TIMESTAMP, Long.class); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<CryptocurrencyData, P>
  {
    public Builder(){ super(); }

    public Builder(final CryptocurrencyData prior)
    {
      super(prior);
    }

    public P key(final String key)
    {
      data(KEY, key);
      return self();
    }

    public P symbol(final String symbol)
    {
      data(SYMBOL, symbol);
      return self();
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P price(final Money price)
    {
      data(PRICE, price);
      return self();
    }

    public P timestamp(final Long timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P volume24H(final Money volume24)
    {
      data(VOLUME_24H, volume24);
      return self();
    }

    public P marketCap(final Money marketCap)
    {
      data(MARKET_CAP, marketCap);
      return self();
    }

    public P availableSupply(final BigDecimal availableSupply)
    {
      data(AVAILABLE_SUPPLY, availableSupply);
      return self();
    }

    public P totalSupply(final BigDecimal totalSupply)
    {
      data(TOTAL_SUPPLY, totalSupply);
      return self();
    }

    public P change1H(final BigDecimal change1H)
    {
      data(CHANGE_1H, change1H);
      return self();
    }

    public P change24H(final BigDecimal change24H)
    {
      data(CHANGE_24H, change24H);
      return self();
    }

    public P change7D(final BigDecimal change7D)
    {
      data(CHANGE_7D, change7D);
      return self();
    }

    public CryptocurrencyData build(){ return new CryptocurrencyData(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final CryptocurrencyData prior)
  {
    return new Builder(prior);
  }
}
