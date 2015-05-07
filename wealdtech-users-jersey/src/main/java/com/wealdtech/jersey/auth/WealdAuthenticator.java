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
import com.sun.jersey.spi.container.ContainerRequest;
import com.wealdtech.*;
import com.wealdtech.authentication.AuthenticationMethod;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.Credentials;
import com.wealdtech.authentication.IdentityAuthenticationMethod;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.jersey.UserHeaders;
import com.wealdtech.services.UserService;

import javax.annotation.Nullable;

import static com.wealdtech.Preconditions.checkState;

/**
 * Weald technology authentication is a four-step process.  These steps are as follows:
 * <ul>
 * <li>Obtain the user's credentials from the request
 * <li>Obtain the user as described by the credentials
 * <li>Authenticate the user with the credentials
 * <li>Change the user to a suitable identity, if requested
 * </ul>
 * Each of these steps is managed by a separate method.  This allows insertion of
 * items such as caching if required
 */
public abstract class WealdAuthenticator implements Authenticator<TwoTuple<User, UserAuthorisation>>
{
  protected final UserService userService;


  public WealdAuthenticator(final UserService userService)
  {
    this.userService = userService;
  }

  public abstract boolean canAuthenticate(final ContainerRequest request);

  /**
   * Obtain the credentials supplied in the request
   * @param request the request
   * @return Credentials as supplied by the request
   */
  public abstract Credentials obtainRequestCredentials(final ContainerRequest request);

  /**
   * Obtain the user given credentials
   * @param credentials the credentials identifying the user
   * @return The user; can be {@code null} if the credentials do not match a user
   */
  @Nullable
  public User obtainUnauthenticatedUser(final Credentials credentials)
  {
    return userService.obtain(credentials);
  }

  /**
   * Authenticate a user against credentials
   * @param request the request
   * @param user the unauthenticated user as defined by the credentials
   * @param credentials the credentials against which to authenticate the user
   * @return authorisation confirming the authenticated user
   */
  public abstract UserAuthorisation authenticateUser(final ContainerRequest request, final User user, final Credentials credentials);

  /**
   * Resolve an identity given an authenticated user and a requested user ID
   * @param user the authenticated user
   * @param identityId the requested user ID
   * @return A two-tuple of the resolved user and credentials
   */
  public TwoTuple<User, UserAuthorisation> resolveIdentity(final User user, final UserAuthorisation userAuthorisation, final WID<User> identityId)
  {
    User identity = user;
    UserAuthorisation identityAuthorisation = userAuthorisation;
    if (identityId != null && !Objects.equal(user.getId(), identityId))
    {
      // Request is for an identity.  Fetch it and confirm that the user is allowed to access it
      identity = userService.obtain(identityId);
      checkState(identity != null, "Unknown user");

      for (final AuthenticationMethod authenticationMethod : identity.getAuthenticationMethods())
      {
        if (Objects.equal(authenticationMethod.getType(), IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION))
        {
          final IdentityAuthenticationMethod identityAuthenticationMethod = WObject.recast(authenticationMethod, IdentityAuthenticationMethod.class);
          if (Objects.equal(identityAuthenticationMethod.getUserId(), user.getId()))
          {
            identityAuthorisation = UserAuthorisation.builder().userId(identity.getId()).scope(identityAuthenticationMethod.getScope()).build();
            break;
          }
        }
      }
      checkState(identityAuthorisation != null && identityAuthorisation.getScope() != AuthorisationScope.NONE, "Not allowed to access that user");
    }
    return new TwoTuple<>(identity, identityAuthorisation);
  }

  @Nullable
  public WID<User> obtainRequestedIdentityId(final ContainerRequest request)
  {
    final String identityIdStr = request.getHeaderValue(UserHeaders.IDENTITY_HEADER);
    try
    {
      return WID.<User>fromString(identityIdStr);
    }
    catch (final DataError de)
    {
      return null;
    }
  }
}
