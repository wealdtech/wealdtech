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
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.DataError;
import com.wealdtech.GenericWObject;
import com.wealdtech.retrofit.RetrofitHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

/**
 * Client for Google ID token API
 */
public class GoogleIdTokenClient
{
  private static final Logger LOG = LoggerFactory.getLogger(GoogleIdTokenClient.class);

  private static final String ENDPOINT = "https://www.googleapis.com/oauth2/v3/";

  private final GoogleIdTokenService service;

  private final ImmutableList<String> clientIds;

  /**
   *
   * @param clientIds the valid Google client IDs; required to verify tokens
   */
  @Inject
  public GoogleIdTokenClient(@Named("googleclientids") final ImmutableList<String> clientIds)
  {
    this.clientIds = clientIds;

    this.service = RetrofitHelper.createRetrofit(ENDPOINT, GoogleIdTokenService.class);
  }

  /**
   * Obtain user details given an ID token
   *
   * @param idToken the ID token
   *
   * @return the user's details
   */
  public GenericWObject obtainInfo(final String idToken)
  {
    final GenericWObject response = RetrofitHelper.call(service.obtainInfo(idToken));

    LOG.debug("Response is {}", response);

    // TODO handle bad response

    final Optional<String> aud = response.get("aud", String.class);
    if (!aud.isPresent())
    {
      throw new DataError.Bad("Missing aud in response");
    }
    if (!clientIds.contains(aud.get()))
    {
      throw new DataError.Bad("Incorrect aud in response");
    }

    final Optional<String> iss = response.get("iss", String.class);
    if (!iss.isPresent())
    {
      throw new DataError.Bad("Missing iss in response");
    }
    if (!(iss.get().equals("accounts.google.com") || iss.get().equals("https://accounts.google.com")))
    {
      throw new DataError.Bad("Incorrect iss in response");
    }

    final Optional<Long> exp = response.get("exp", Long.class);
    if (!exp.isPresent())
    {
      throw new DataError.Bad("Missing exp in response");
    }
    if (exp.get() < DateTime.now().getMillis() / 1000)
    {
      throw new DataError.Bad("Incorrect exp in response");
    }

    // At this stage the result has passed verification

    final GenericWObject.Builder<?> builder = GenericWObject.builder();
    builder.data("source", "google");

    final Optional<String> sub = response.get("sub", String.class);
    if (sub.isPresent())
    {
      builder.data("id", sub.get());
    }

    final Optional<String> email = response.get("email", String.class);
    if (email.isPresent())
    {
      builder.data("email", email.get());
    }

    final Optional<String> name = response.get("name", String.class);
    if (name.isPresent())
    {
      builder.data("name", name.get());
    }

    final Optional<String> picture = response.get("picture", String.class);
    if (picture.isPresent())
    {
      builder.data("picture", picture.get());
    }

    final Optional<String> locale = response.get("locale", String.class);
    if (locale.isPresent())
    {
      builder.data("locale", locale.get());
    }

    return builder.build();
  }
}
