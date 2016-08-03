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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.configuration.OAuth2Configuration;
import com.wealdtech.retrofit.RetrofitHelper;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * A client that talks to Stripe
 */
public class StripeClient
{
  private static final Logger LOG = LoggerFactory.getLogger(StripeClient.class);

  private static final String ENDPOINT = "https://connect.stripe.com/oauth/";

  private final OAuth2Configuration configuration;

  private final StripeService service;

  @Inject
  public StripeClient(final OAuth2Configuration configuration)
  {
    this.configuration = configuration;

    this.service = RetrofitHelper.createRetrofit(ENDPOINT, StripeService.class);
  }

  /**
   * @return the authorisation URI for this client
   */
  public URI generateAuthorisationUri(final String state)
  {
    return URI.create(ENDPOINT + "authorize?response_type=code&scope=read_write&client_id=" + configuration.getClientId() + "&state=" + state);
  }

  /**
   * Carry out authorisation given an authorisation callback from Stripe.
   * This contacts Stripe's servers to obtain suitable credentials for the user
   * @param name the name to be used for the resultant credentials
   * @param uri the authorisation callback URI
   * @return a two tuple containing the state string from the callback URI and the resultant credentials
   */
  public TwoTuple<String, OAuth2Credentials> auth(final String name, final URI uri)
  {
    final ImmutableMultimap<String, String> params = splitQuery(uri);

    if (params.containsKey("error"))
    {
      // An error occurred
      if (params.containsKey("error_description"))
      {
        throw new ClientError("Failed to authorize: " + params.get("error_description").iterator().next());
      }
      else
      {
        throw new ClientError("Failed to authorize: " + params.get("error").iterator().next());
      }
    }

    if (!params.containsKey("code"))
    {
      throw new ClientError("Bad authorisation URI missing code");
    }
    final String code = params.get("code").iterator().next();

    if (!params.containsKey("scope"))
    {
      throw new ClientError("Bad authorisation URI missing scope");
    }
    final String scope = params.get("scope").iterator().next();

    if (!params.containsKey("state"))
    {
      throw new ClientError("Bad authorisation URI missing state");
    }
    final String state = params.get("state").iterator().next();

    // Authorise using the code we have been given
    final GenericWObject response = RetrofitHelper.call(service.obtainToken("authorization_code", configuration.getSecret(), code));

    LOG.debug("Response is {}", response);

    // TODO handle bad response

    final Optional<String> accessToken = response.get("access_token", String.class);
    if (!accessToken.isPresent())
    {
      throw new DataError.Bad("Missing access token in response");
    }

    final Optional<String> refreshToken = response.get("refresh_token", String.class);
    if (!refreshToken.isPresent())
    {
      throw new DataError.Bad("Missing refresh token in response");
    }

    final Optional<String> stripePublishableKey = response.get("stripe_publishable_key", String.class);
    if (!stripePublishableKey.isPresent())
    {
      throw new DataError.Bad("Missing stripe publishable key in response");
    }

    final Optional<String> stripeUserId = response.get("stripe_user_id", String.class);
    if (!stripeUserId.isPresent())
    {
      throw new DataError.Bad("Missing stripe user ID in response");
    }

    final OAuth2Credentials credentials = OAuth2Credentials.builder()
                                                           .name(name)
                                                           .accessToken(accessToken.get())
                                                           .expires(
                                                               LocalDateTime.now().plusYears(15)) // Stripe token doesn't expire
                                                           .scopes(ImmutableSet.of(scope))
                                                           .refreshToken(refreshToken.get())
                                                           // Additional data for Stripe
                                                           .data("stripepublishablekey", stripePublishableKey.get())
                                                           .data("stripeuserid", stripeUserId.get())
                                                           .build();

    return new TwoTuple<>(state, credentials);
  }

  /**
   * Carry out re-authorisation of OAuth2 with
   *
   * @param credentials the current OAuth2 credentials
   *
   * @return A refreshed OAuth2 credential
   */
  public OAuth2Credentials reauth(final OAuth2Credentials credentials)
  {
    throw new ServerError("Not implemented");
    //    if (credentials == null) { return null; }
//
//    final GenericWObject response = RetrofitHelper.call(
//        service.refreshToken("refresh_token", configuration.getSecret(),
//                             credentials.getRefreshToken()));
//
//    LOG.debug("Response is {}", response);
//
//    // TODO handle bad response
//
//    final Optional<String> accessToken = response.get("access_token", String.class);
//    if (!accessToken.isPresent())
//    {
//      throw new DataError.Bad("Missing access token in response");
//    }
//
//    final Optional<Integer> ttl = response.get("expires_in", Integer.class);
//    if (!ttl.isPresent())
//    {
//      throw new DataError.Bad("Missing ttl in response");
//    }
//
//    return OAuth2Credentials.builder(credentials)
//                            .accessToken(accessToken.get())
//                            .expires(DateTime.now().plusSeconds(ttl.get()))
//                            .build();
  }

  private static ImmutableMultimap<String, String> splitQuery(URI uri)
  {
    final ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
    try
    {
      final String[] pairs = uri.getQuery().split("&");
      for (String pair : pairs)
      {
        final int idx = pair.indexOf("=");
        final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
        final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
        builder.put(key, value);
      }
    }
    catch (final UnsupportedEncodingException usee)
    {
      LOG.debug("Bad item in query string: ", usee);
    }
    return builder.build();
  }
}
