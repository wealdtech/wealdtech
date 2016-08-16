/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mangopay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

public class CardRegistration extends WObject<CardRegistration> implements Comparable<CardRegistration>
{
  private static final String REGISTRATION_ID = "registrationid";
  private static final String ACCESS_KEY = "accesskey";
  private static final String PRE_REGISTRATION_DATA = "preregistrationdata";
  private static final String CARD_REGISTRATION_URL = "cardregistrationurl";

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(REGISTRATION_ID)) { throw new DataError.Missing("Card registration failed validation: missing registration ID"); }
    if (!exists(ACCESS_KEY)) { throw new DataError.Missing("Card registration failed validation: missing access key"); }
    if (!exists(PRE_REGISTRATION_DATA)) { throw new DataError.Missing("Card registration failed validation: missing pre-registration data"); }
    if (!exists(CARD_REGISTRATION_URL)) { throw new DataError.Missing("Card registration failed validation: missing card registration URL"); }
  }

  @JsonCreator
  public CardRegistration(final Map<String, Object> data) { super(data); }

  @JsonIgnore
  public String getRegistrationId() { return get(REGISTRATION_ID, String.class).get(); }

  @JsonIgnore
  public String getAccessKey() { return get(ACCESS_KEY, String.class).get(); }

  @JsonIgnore
  public String getPreRegistrationData() { return get(PRE_REGISTRATION_DATA, String.class).get(); }

  @JsonIgnore
  public String getCardRegistrationUrl() { return get(CARD_REGISTRATION_URL, String.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<CardRegistration, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final CardRegistration prior)
    {
      super(prior);
    }

    public P registrationId(final String registrationId)
    {
      data(REGISTRATION_ID, registrationId);
      return self();
    }

    public P accessKey(final String accessKey)
    {
      data(ACCESS_KEY, accessKey);
      return self();
    }

    public P preRegistrationData(final String preRegistrationData)
    {
      data(PRE_REGISTRATION_DATA, preRegistrationData);
      return self();
    }

    public P cardRegistrationUrl(final String cardRegistrationUrl)
    {
      data(CARD_REGISTRATION_URL, cardRegistrationUrl);
      return self();
    }
    public CardRegistration build()
    {
      return new CardRegistration(data);
    }
  }

  public static Builder<?> builder() { return new Builder(); }

  public static Builder<?> builder(final CardRegistration prior) { return new Builder(prior); }

}
