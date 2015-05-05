/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.providers;

import com.sun.jersey.api.core.HttpContext;
import com.wealdtech.DataError;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.jersey.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * Provide user authorisation to resources.
 * <p/>To access the authorisation add the following as either a method argument or class field:
 * <p/><code>@Context UserAuthorisation userAuthorisation</code>
 * <p/>This provider ensures that supplied authorisation is valid, so its presence in a method signature means that the request
 * will fail if a request has not been successfully authenticated.
 * <p/>The heavy lifting for this provider is carried out in the authentication; we just make the information available.
 */
@Provider
public class UserAuthorisationProvider extends AbstractInjectableProvider<UserAuthorisation>
{
  @Context
  private HttpServletRequest servletRequest;

  public UserAuthorisationProvider()
  {
    super(UserAuthorisation.class);
  }

  /**
   * Provide the user authorisation.
   */
  @Override
  public UserAuthorisation getValue(final HttpContext c)
  {
    final UserAuthorisation userAuthorisation = (UserAuthorisation)servletRequest.getAttribute("com.wealdtech.authenticatedprincipal.authorisation");
    try
    {
      checkNotNull(userAuthorisation, "Unauthorized");
      checkState(userAuthorisation.getScope() != AuthorisationScope.NONE, "Unauthorized");
    }
    catch (final DataError de)
    {
      throw new UnauthorizedException(de);
    }
    return userAuthorisation;
  }
}
