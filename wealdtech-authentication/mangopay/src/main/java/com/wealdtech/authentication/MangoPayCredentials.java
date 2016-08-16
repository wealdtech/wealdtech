/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * MangoPay credentials for authentication.
 */
public class MangoPayCredentials extends Credentials
{
  public static final String MANGOPAY_CREDENTIALS = "MangoPay";

  private static final Logger LOG = LoggerFactory.getLogger(MangoPayCredentials.class);

  private static final String NAME = "name";
  private static final String USER_ID = "userid";
  private static final String WALLET_ID = "walletid";
  private static final String CURRENCY = "currency";
  private static final String CARD_ID = "cardid";

  private static final TypeReference<ImmutableSet<String>> SCOPES_TYPE_REF = new TypeReference<ImmutableSet<String>>() {};

  @JsonCreator
  public MangoPayCredentials(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "MangoPay authentication method failed validation: must contain name");
    checkState(exists(USER_ID), "MangoPay authentication method failed validation: must contain userid");
    checkState(exists(WALLET_ID), "MangoPay authentication method failed validation: must contain walletid");
    checkState(exists(CURRENCY), "MangoPay authentication method failed validation: must contain currency");
  }

  @JsonIgnore
  public String getType(){return MANGOPAY_CREDENTIALS;}

  @JsonIgnore
  public String getName(){return get(NAME, String.class).get();}

  @JsonIgnore
  public String getUserId(){return get(USER_ID, String.class).get();}

  @JsonIgnore
  public String getWalletId(){return get(WALLET_ID, String.class).get();}

  @JsonIgnore
  public Currency getCurrency() { return get(CURRENCY, Currency.class).get(); }

  @JsonIgnore
  public Optional<String> getCardId(){return get(CARD_ID, String.class);}

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends Credentials.Builder<P>
  {
    public Builder()
    {
      super();
      data(TYPE, MANGOPAY_CREDENTIALS);
    }

    public Builder(final MangoPayCredentials prior)
    {
      super(prior);
      data(TYPE, MANGOPAY_CREDENTIALS);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P userId(final String userId)
    {
      data(USER_ID, userId);
      return self();
    }

    public P walletId(final String walletId)
    {
      data(WALLET_ID, walletId);
      return self();
    }

    public P currency(final Currency currency)
    {
      data(CURRENCY, currency);
      return self();
    }

    public P cardId(final String cardId)
    {
      data(CARD_ID, cardId);
      return self();
    }

    public MangoPayCredentials build(){ return new MangoPayCredentials(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final MangoPayCredentials prior)
  {
    return new Builder(prior);
  }
}
