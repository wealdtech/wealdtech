/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.auth;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.wealdtech.*;
import com.wealdtech.authentication.AuthenticationMethod;
import com.wealdtech.authentication.Credentials;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.authentication.PasswordCredentials;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.services.UserService;
import com.wealdtech.utils.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * Authenticate a Weald user with Basic HTTP authentication
 */
public final class WealdBasicAuthenticator extends WealdAuthenticator
{
  private static final Logger LOG = LoggerFactory.getLogger(WealdBasicAuthenticator.class);

  private static final Splitter BASICSPLITTER = Splitter.onPattern("\\s+").limit(2);

  @Inject
  private WealdBasicAuthenticator(final UserService userService)
  {
    super(userService);
  }

  @Override
  public boolean canAuthenticate(final ContainerRequest request)
  {
    boolean result = false;
    try
    {
      splitAuthorizationHeader(request.getHeaderValue(ContainerRequest.AUTHORIZATION));
      result = true;
    }
    catch (final DataError de)
    {
      // This happens if the request is not authenticated using Basic
    }
    return result;
  }

  /**
   * Authenticate the user.  If the credentials include an identity ID then this identity is the user which will be passed back (if
   * anything)
   *
   * @return A two-tuple containing the authenticated user and credentials; can be <code>absent</code>
   * @throws ServerError if there is a problem authenticating the credentials
   */
  @Override
  public Optional<TwoTuple<User, UserAuthorisation>> authenticate(final ContainerRequest request)
  {
    final Credentials credentials = obtainRequestCredentials(request);
    final WID<User> identityId = obtainRequestedIdentityId(request);

    final Optional<TwoTuple<User, UserAuthorisation>> result;
    try
    {
      final User unauthenticatedUser = obtainUnauthenticatedUser(credentials);
      if (unauthenticatedUser == null)
      {
        throw new DataError.Bad("Credentials do not relate to a user");
      }

      final UserAuthorisation authorisation = authenticateUser(request, unauthenticatedUser, credentials);

      // At this stage the user has been authenticated
      final User user = unauthenticatedUser;

      // Resolve the user to their identity and return them
      result = Optional.of(resolveIdentity(user, authorisation, identityId));
    }
    catch (final DataError.Bad de)
    {
      LOG.debug("Failed to obtain user", de);
      throw de;
    }

    return result;
  }

  @Override
  public Credentials obtainRequestCredentials(final ContainerRequest request)
  {
    final ImmutableMap<String, String> headers = splitAuthorizationHeader(request.getHeaderValue(ContainerRequest.AUTHORIZATION));
    checkNotNull(headers.get("username"), "Missing username");
    checkNotNull(headers.get("password"), "Missing password");
    return PasswordCredentials.builder().name(headers.get("username")).password(headers.get("password")).build();
  }

  @Override
  public UserAuthorisation authenticateUser(final ContainerRequest request, final User user, final Credentials credentials)
  {
    final PasswordCredentials passwordCredentials = (PasswordCredentials)credentials;
    for (final AuthenticationMethod authenticationMethod : user.getAuthenticationMethods())
    {
      if (Objects.equal(authenticationMethod.getType(), PasswordCredentials.PASSWORD_CREDENTIALS))
      {
        final PasswordAuthenticationMethod passwordAuthenticationMethod =
            WObject.recast(authenticationMethod, PasswordAuthenticationMethod.class);
        if (Hash.matches(passwordCredentials.getPassword(), passwordAuthenticationMethod.getPassword()) &&
            !authenticationMethod.hasExpired())
        {
          return UserAuthorisation.builder().userId(user.getId()).scope(passwordAuthenticationMethod.getScope()).build();
        }
      }
    }
    LOG.info("Failed to authenticate user {} with password authentication", user.getId().toString());
    throw new DataError.Authentication("Failed to authenticate user");
  }

  private ImmutableMap<String, String> splitAuthorizationHeader(final String authorizationHeader)
  {
    checkNotNull(authorizationHeader);
    final List<String> headerFields = Lists.newArrayList(BASICSPLITTER.split(authorizationHeader));
    if (headerFields.size() != 2)
    {
      throw new DataError.Bad("Authorization header missing");
    }
    if (!"basic".equals(headerFields.get(0).toLowerCase(Locale.ENGLISH)))
    {
      throw new DataError.Bad("Not a Basic authorization header");
    }

    final String[] values = Base64.base64Decode(headerFields.get(1)).split(":");
    if (values.length != 2)
    {
      throw new DataError.Bad("Authorization header does not contain username and password");
    }

    final Map<String, String> fields = Maps.newHashMap();
    fields.put("username", values[0]);
    fields.put("password", values[1]);
    return ImmutableMap.copyOf(fields);
  }
}
