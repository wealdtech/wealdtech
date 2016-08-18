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
import com.wealdtech.CreditCard;
import com.wealdtech.GenericWObject;
import com.wealdtech.Money;
import com.wealdtech.config.MangoPayConfiguration;
import com.wealdtech.mangopay.CardRegistration;
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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;

import static com.wealdtech.Preconditions.checkState;

/**
 * Client for the MangoPay service
 */
public class MangoPayClient
{
  private static final Logger LOG = LoggerFactory.getLogger(MangoPayClient.class);

  private final MangoPayConfiguration configuration;
  private final MangoPayService service;

  @Inject
  public MangoPayClient(final MangoPayConfiguration configuration)
  {
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
      final Response<GenericWObject> response =
          service.ping(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId()).execute();
      if (response.isSuccessful())
      {
        final Optional<String> clientId = response.body().get("ClientId", String.class);
        if (clientId.isPresent() && Objects.equal(clientId.get(), configuration.getClientId()))
        {
          success = true;
        }
      }
    }
    catch (final IOException ignored) {}

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
          service.createUser(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                             builder.build()).execute();
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

  public String createWallet(final String userId, final Currency currency, final String description, final String tag)
  {
    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    if (tag != null)
    {
      builder.data("Tag", tag);
    }
    builder.data("owners", Collections.singletonList(userId));
    builder.data("description", description);
    builder.data("currency", currency.toString());

    String walletId = null;
    try
    {
      final Response<GenericWObject> response =
          service.createWallet(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                               builder.build()).execute();
      if (!response.isSuccessful())
      {
        LOG.error("Failed to create wallet: {} ", response.errorBody().string());
      }
      else
      {
        final Optional<String> id = response.body().get("Id", String.class);
        if (!id.isPresent())
        {
          LOG.error("Failed to create wallet; response is {}", response.body().toString());
        }
        else
        {
          walletId = id.get();
        }
      }
    }
    catch (final IOException ioe)
    {
      LOG.error("IO exception: ", ioe);
    }
    return walletId;
  }

  /**
   * @param userId the MangoPay ID of the user registering the card
   * @param brand the brand of the card being registered
   * @param currency the currency that payments from this card will be taken
   * @param tag an optional tag for the registration
   *
   * @return a card registration object
   */
  public CardRegistration createCardRegistration(final String userId,
                                                 final CreditCard.Brand brand,
                                                 final Currency currency,
                                                 final String tag)
  {
    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    if (tag != null)
    {
      builder.data("Tag", tag);
    }
    builder.data("UserId", userId);
    builder.data("Currency", currency.toString());
    builder.data("CardType", brandToType(brand));

    final GenericWObject response = RetrofitHelper.call(
        service.createCardRegistration(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                                       builder.build()));

    final CardRegistration.Builder<?> resultB = CardRegistration.builder();
    checkState(response.get("Id", String.class).isPresent(), "Response failed to provide Id");
    resultB.registrationId(response.get("Id", String.class).get());
    checkState(response.get("AccessKey", String.class).isPresent(), "Response failed to provide AccessKey");
    resultB.accessKey(response.get("AccessKey", String.class).get());
    checkState(response.get("PreregistrationData", String.class).isPresent(), "Response failed to provide PreregistrationData");
    resultB.preRegistrationData(response.get("PreregistrationData", String.class).get());
    checkState(response.get("CardRegistrationURL", String.class).isPresent(), "Response failed to provide CardRegistrationURL");
    resultB.cardRegistrationUrl(response.get("CardRegistrationURL", String.class).get());

    return resultB.build();
  }

  /**
   * Obtain an active card registration
   *
   * @param registrationId the ID of the card registration
   *
   * @return the card registration
   */
  public GenericWObject obtainCardRegistration(final String registrationId)
  {
    return RetrofitHelper.call(
        service.obtainCardRegistration(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                                       registrationId));
  }

  /**
   * Complete the card registration process
   *
   * @param userId the ID of the user completing the registration process
   * @param registrationId the ID of the card registration
   * @param data the data returned to the local client after tokenising the credit card details
   *
   * @return the ID of the registered card
   */
  public String completeCardRegistration(final String userId, final String registrationId, final String data)
  {
    // Ensure that this is a valid registration for completion
    final GenericWObject registration = obtainCardRegistration(registrationId);

    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    builder.data("RegistrationData", data);

    final GenericWObject response = RetrofitHelper.call(
        service.updateCardRegistration(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                                       registrationId, builder.build()));

    checkState(response != null, "Failed to register card");
    final Optional<String> cardId = response.get("CardId", String.class);
    checkState(cardId.isPresent(), "Failed to obtain card ID");

    return cardId.get();
  }

  public String payIn(final String senderId,
                      final String senderCardId,
                      final String recipientId,
                      final String recipientWalletId,
                      final Money funds,
                      final Money fees)
  {
    // Ensure that the details of the sender and recipient are correct
    final GenericWObject sender = RetrofitHelper.call(
        service.obtainUser(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(), senderId));
    System.err.println(sender);
    checkState(sender != null, "Failed to obtain sender information");

    final GenericWObject senderCard = RetrofitHelper.call(
        service.obtainCard(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                           senderCardId));
    System.err.println(senderCard);
    checkState(senderCard != null, "failed to obtain sender credit card details");
    // TODO further validation of card - active, anything else?

    final GenericWObject recipient = RetrofitHelper.call(
        service.obtainUser(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(), recipientId));
    System.err.println(recipient);
    checkState(recipient != null, "failed to obtain recipient information");

    final GenericWObject recipientWallet = RetrofitHelper.call(
        service.obtainWallet(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                             recipientWalletId));
    System.err.println(recipientWallet);
    checkState(recipientWallet != null, "failed to obtain recipient wallet");

    // Ensure that everything uses the same currency
    final Currency senderCardCurrency = senderCard.get("Currency", Currency.class).orNull();
    checkState(senderCardCurrency != null, "Sender credit card has no currency information");
    final Currency recipientWalletCurrency = recipientWallet.get("Currency", Currency.class).orNull();
    checkState(recipientWalletCurrency != null, "Recipient wallet has no currency information");
    checkState(Objects.equal(senderCardCurrency, recipientWalletCurrency),
               "Sender credit card currency does not match recipient wallet currency");
    checkState(Objects.equal(senderCardCurrency, funds.getCurrency()), "Sender credit card currency does not match funds currency");
    checkState(Objects.equal(senderCardCurrency, fees.getCurrency()), "Sender credit card currency does not match fees currency");

    // Create the payin object
    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    builder.data("AuthorId", senderId);
    builder.data("CreditedUserId", recipientId);
    builder.data("CardId", senderCardId);
    builder.data("CreditedWalletId", recipientWalletId);
    final GenericWObject fundsObj = GenericWObject.builder()
                                                  .data("Currency", funds.getCurrency())
                                                  .data("Amount", funds.getAmount().multiply(new BigDecimal(100)).toBigIntegerExact().toString())
                                                  .build();
    builder.data("DebitedFunds", fundsObj);
    final GenericWObject feesObj = GenericWObject.builder()
                                                 .data("Currency", fees.getCurrency())
                                                 .data("Amount", fees.getAmount().multiply(new BigDecimal(100)).toBigIntegerExact().toString())
                                                 .build();
    builder.data("Fees", feesObj);
    builder.data("SecureModeReturnURL", "http://localhost/");
    builder.data("SecureMode", "DEFAULT");

    final GenericWObject result = RetrofitHelper.call(
        service.createDirectPayIn(auth(configuration.getClientId(), configuration.getSecret()), configuration.getClientId(),
                                  builder.build()));

    System.err.println(result);
    return result.get("Id", String.class).orNull();
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

  /**
   * Supply the MangoPay credit card type given Weald's brand
   *
   * @param brand the brand
   *
   * @return the MangoPay type; can be {@code null}
   */
  private static String brandToType(final CreditCard.Brand brand)
  {
    if (brand == null) { return null; }
    switch (brand)
    {
      case AMERICAN_EXPRESS:
        return "AMEX";
      case DINERS_CLUB:
        return "DINERS";
      case CHINA_UNIONPAY:
      case DISCOVER:
      case JCB:
      case MASTERCARD:
      case VISA:
        return "CB_VISA_MASTERCARD";
      default:
        return null;
    }
  }
}
