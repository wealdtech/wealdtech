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

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A payment is a single transfer of money from one user to another
 */
public class Payment extends WObject<Payment> implements Comparable<Payment>
{
  private static final String ORIGINATOR = "originator";
  private static final String RECIPIENT = "recipient";
  private static final String DESCRIPTION = "description";
  private static final String VALUE = "value";

  @JsonCreator
  public Payment(final Map<String, Object> data) { super(data); }

  @Override
  public void validate()
  {
    checkState(exists(ID), "Payment failed validation: must contain id");
    checkState(exists(ORIGINATOR), "Payment failed validation: must contain originator");
    checkState(exists(RECIPIENT), "Payment failed validation: must contain recipient");
    checkState(exists(VALUE), "Payment failed validation: must contain value");
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<Payment> getId(){ return super.getId(); }

  @JsonIgnore
  public String getOriginator() { return get(ORIGINATOR, String.class).get(); }

  @JsonIgnore
  public String getRecipient() { return get(RECIPIENT, String.class).get(); }

  @JsonIgnore
  public Money getValue() { return get(VALUE, Money.class).get(); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Payment, P>
  {
    public Builder(){ super(); }

    public Builder(final Payment prior)
    {
      super(prior);
    }

    public P originator(final String originator)
    {
      data(ORIGINATOR, originator);
      return self();
    }

    public P recipient(final String recipient)
    {
      data(RECIPIENT, recipient);
      return self();
    }

    public P description(final String description)
    {
      data(DESCRIPTION, description);
      return self();
    }

    public P value(final Money value)
    {
      data(VALUE, value);
      return self();
    }

    public Payment build(){ return new Payment(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final Payment prior)
  {
    return new Builder(prior);
  }
}
