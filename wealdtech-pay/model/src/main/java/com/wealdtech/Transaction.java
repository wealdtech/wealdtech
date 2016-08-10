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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A transaction is a list of payments
 */
public class Transaction extends WObject<Transaction> implements Comparable<Transaction>
{
  private static final String PAYMENTS = "payments";
  
  @JsonCreator
  public Transaction(final Map<String, Object> data) { super(data); }
  
  @Override
  protected void validate()
  {
    checkState(exists(ID), "Transaction failed validation: must contain ID");
    checkState(exists(PAYMENTS), "Transaction failed validation: must contain payments");
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<Transaction> getId(){ return super.getId(); }

  private static final TypeReference<ImmutableList<Payment>> PAYMENTS_TYPE_REF = new TypeReference<ImmutableList<Payment>>(){};
  
  @JsonIgnore
  public ImmutableList<Payment> getPayments() { return get(PAYMENTS, PAYMENTS_TYPE_REF).get(); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Transaction, P>
  {
    public Builder(){ super(); }

    public Builder(final Transaction prior)
    {
      super(prior);
    }

    public P payments(final ImmutableList<Payment> payments)
    {
      data(PAYMENTS, payments);
      return self();
    }

    public Transaction build(){ return new Transaction(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final Transaction prior)
  {
    return new Builder(prior);
  }
}
