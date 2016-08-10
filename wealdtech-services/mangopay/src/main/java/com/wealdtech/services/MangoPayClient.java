/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.mangopay.MangoPayApi;
import com.mangopay.core.enumerations.CountryIso;
import com.mangopay.core.enumerations.CurrencyIso;
import com.mangopay.entities.CardRegistration;
import com.wealdtech.GenericWObject;
import com.wealdtech.config.MangoPayConfiguration;
import com.wealdtech.retrofit.RetrofitHelper;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Client for the MangoPay service
 */
public class MangoPayClient
{
  private static final Logger LOG = LoggerFactory.getLogger(MangoPayClient.class);

  private final MangoPayConfiguration configuration;
  private final MangoPayService service;

  private final MangoPayApi mangoService;

  @Inject
  public MangoPayClient(final MangoPayConfiguration configuration)
  {
    this.mangoService = new MangoPayApi();
    this.mangoService.Config.ClientId = configuration.getClientId();
    this.mangoService.Config.ClientPassword = configuration.getSecret();
    this.mangoService.Config.BaseUrl = configuration.getEndpoint().toString();

    this.configuration = configuration;
    this.service = RetrofitHelper.createRetrofit(configuration.getEndpoint().toString(), MangoPayService.class);
  }

  /**
   * Ping the MangoPay service to see if it is responding.
   *
   * @return {@code true} if the service is responding, otherwise {@code false}.
   */
  public boolean ping()
  {
    boolean success = false;
    try
    {
      final Response<GenericWObject>
          response = service.ping(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId()).execute();
      if(response.isSuccessful())
      {
        final Optional<String> clientId = response.body().get("ClientId", String.class);
        if (clientId.isPresent() && Objects.equal(clientId.get(), configuration.getClientId()))
        {
          success = true;
        }
      }
    }
    catch (final IOException ignored) {}
//    try
//    {
//      final Client client = mangoService.Clients.get();
//      if (Objects.equal(client.ClientId, mangoService.Config.ClientId))
//      {
//        success = true;
//      }
//    }
//    catch (final Exception e)
//    {
//      LOG.error("Exception occurred: ", e);
//    }

    return success;
  }

  /**
   * Create a user
   *
   * @param firstName
   * @param lastName
   * @param email
   * @param dateOfBirth
   * @param nationality
   * @param residence
   * @param tag
   *
   * @return the user's ID if created, otherwise {@code null}
   */
  public String createUser(final String firstName,
                           final String lastName,
                           final String email,
                           final LocalDate dateOfBirth,
                           final String nationality,
                           final String residence,
                           final String tag)
  {
//    final UserNatural user = new UserNatural();
//    user.FirstName = firstName;
//    user.LastName = lastName;
//    user.Email = email;
//    user.Birthday = dateOfBirth.toDateTime(LocalTime.MIDNIGHT, DateTimeZone.UTC).getMillis() / 1000L;
//    user.Nationality = countryIsoFromString(nationality);
//    user.CountryOfResidence = countryIsoFromString(residence);
//    user.Tag = tag;
//
//    try
//    {
//      final User createdUser = mangoService.Users.create(user);
//      return createdUser.Id;
//    }
//    catch (final Exception e)
//    {
//      LOG.error("Exception occurred: ", e);
//    }
//    return null;
    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    if (tag != null)
    {
      builder.data("Tag", tag);
    }
    builder.data("FirstName", firstName);
    builder.data("LastName", lastName);
    builder.data("Email", email);
    builder.data("Birthday", dateOfBirth.toDateTime(LocalTime.MIDNIGHT, DateTimeZone.UTC).getMillis() / 1000L);
    builder.data("Nationality", nationality);
    builder.data("CountryOfResidence", residence);

    String userId = null;
    try
    {
      final Response<GenericWObject> response =
          service.createUser(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(), builder.build()).execute();
      if (!response.isSuccessful())
      {
        LOG.error("Failed to create user: {} ", response.errorBody().string());
      }
      else
      {
        final Optional<String> id = response.body().get("Id", String.class);
        if (!id.isPresent())
        {
          LOG.error("Failed to create user; response is {}", response.body().toString());
        }
        else
        {
          userId = id.get();
        }
      }
    }
    catch (final IOException ioe)
    {
      LOG.error("IO exception: ", ioe);
    }
    return userId;
  }

  public GenericWObject createCardRegistration(final String userId, final String currency)
  {
    final CardRegistration cardRegistration = new CardRegistration();
    cardRegistration.UserId = userId;
    cardRegistration.Currency = currencyIsoFromString(currency);

    try
    {
      final CardRegistration createdCardRegistration = mangoService.CardRegistrations.create(cardRegistration);
      final GenericWObject.Builder<?> builder = GenericWObject.builder();
      builder.data("accesskey", createdCardRegistration.AccessKey);
      builder.data("baseurl", mangoService.Config.BaseUrl);
      builder.data("cardpreregistrationid", createdCardRegistration.Id);
      builder.data("cardregistrationurl", createdCardRegistration.CardRegistrationURL);
      builder.data("cardtype", createdCardRegistration.CardType);
      builder.data("preregistrationdata", cardRegistration.PreregistrationData);
      return builder.build();
    }
    catch (final Exception e)
    {
      LOG.error("Exception occurred: ", e);
    }
    return null;
  }

  private static CountryIso countryIsoFromString(final String str)
  {
    return CountryIso.valueOf(str);
  }

  private static CurrencyIso currencyIsoFromString(final String str)
  {
    return CurrencyIso.valueOf(str);
  }

  private static String auth(final String username, final String password)
  {
    try
    {
      return "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes("UTF-8"));
    }
    catch (final UnsupportedEncodingException uee)
    {
      LOG.error("username and/or password not in UTF-8: ", uee);
      return null;
    }
  }
}
