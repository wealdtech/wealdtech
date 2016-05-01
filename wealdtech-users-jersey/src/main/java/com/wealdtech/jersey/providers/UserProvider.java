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
import com.wealdtech.User;
import com.wealdtech.jersey.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * A sample provider using the information obtained during the authentication process.
 * <p/>To access the user add the following as either a method argument or class field:
 * <p/><code>@Context User user</code>
 * <p/>Note that it is possible for the user to be <code>NULL</code> in the case where a request does not contain credentials
 */
@Provider
public class UserProvider extends AbstractInjectableProvider<User>
{
  @Context
  private HttpServletRequest servletRequest;

  public UserProvider()
  {
    super(User.class);
  }

  @Override
  public User getValue(final HttpContext c)
  {
    final User user = (User)this.servletRequest.getAttribute("com.wealdtech.authenticatedprincipal");
    if (user == null)
    {
      throw new UnauthorizedException("Unauthorized", "Unknown user or incorrect login");
    }
    return user;
  }
}
