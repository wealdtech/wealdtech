/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;
import com.wealdtech.ClientError;
import com.wealdtech.DataError;
import com.wealdtech.GenericWObject;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.configuration.OAuth2Configuration;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * Client for Google Places API
 */
public class GoogleAccountsClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GoogleAccountsClient.class);

  private static final String ENDPOINT = "https://accounts.google.com/o/oauth2";

  private final OAuth2Configuration configuration;

  private final GoogleAccountsService service;

  @Inject
  public GoogleAccountsClient(final OAuth2Configuration configuration)
  {
    this.configuration = configuration;

    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setLogLevel(RestAdapter.LogLevel.FULL).setConverter(converter).build();

    this.service = adapter.create(GoogleAccountsService.class);
  }

  /**
   * Carry out initial authorisation of OAuth2 response and obtain an access token
   *
   * @param uri the full URI that was called to trigger this authorisation
   *
   * @return An OAuth2 credential
   */
  public OAuth2Credentials auth(final String name, final URI uri)
  {
    final ImmutableMultimap<String, String> params = splitQuery(uri);
    if (!params.containsKey("code"))
    {
      throw new ClientError("Bad authorisation URI missing code");
    }
    final String code = params.get("code").iterator().next();
    if (!params.containsKey("state"))
    {
      throw new ClientError("Bad authorisation URI missing state");
    }
    final String state = params.get("state").iterator().next();

    final GenericWObject response =
        service.obtainToken("authorization_code", configuration.getClientId(), configuration.getSecret(),
                            configuration.getCallbackUrl().toString(), code);

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

    final Optional<Integer> ttl = response.get("expires_in", Integer.class);
    if (!ttl.isPresent())
    {
      throw new DataError.Bad("Missing ttl in response");
    }

    return OAuth2Credentials.builder()
                            .name(name)
                            .accessToken(accessToken.get())
                            .expires(DateTime.now().plusSeconds(ttl.get()))
                            .refreshToken(refreshToken.get())
                            .build();
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
    if (credentials == null) { return null; }

    final GenericWObject response = service.refreshToken("refresh_token", configuration.getClientId(), configuration.getSecret(),
                                                         credentials.getRefreshToken());

    LOG.debug("Response is {}", response);

    // TODO handle bad response

    final Optional<String> accessToken = response.get("access_token", String.class);
    if (!accessToken.isPresent())
    {
      throw new DataError.Bad("Missing access token in response");
    }

    final Optional<Integer> ttl = response.get("expires_in", Integer.class);
    if (!ttl.isPresent())
    {
      throw new DataError.Bad("Missing ttl in response");
    }

    return OAuth2Credentials.builder(credentials)
                            .accessToken(accessToken.get())
                            .expires(DateTime.now().plusSeconds(ttl.get()))
                            .build();
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
