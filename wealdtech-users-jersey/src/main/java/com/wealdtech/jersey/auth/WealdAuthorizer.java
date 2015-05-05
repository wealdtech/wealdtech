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

import com.wealdtech.User;
import com.wealdtech.WID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Principal;

/**
 * Handle authorisation for Wealdtech users
 */
public class WealdAuthorizer implements SecurityContext
{
  private static final Logger LOG = LoggerFactory.getLogger(WealdAuthorizer.class);

  @Context
  private UriInfo uriInfo;

  private final WID<User> userId;
  private final Principal principal;

  public WealdAuthorizer(final WID<User> userId)
  {
    this.userId = userId;
    this.principal = new Principal()
    {
      @Override
      public String getName()
      {
        return userId == null ? null : userId.toString();
      }
    };
  }

  @Override
  public Principal getUserPrincipal()
  {
    return this.principal;
  }

  /**
   * Check to see if a user is in a given role.
   * A role equates to a usergroup.  So this actually checks to
   * see if the current user is a member of the usergroup represented
   * by this UUID
   * @param role a UUID representing a usergroup
   */
  @Override
  public boolean isUserInRole(final String role)
  {
    return false;
  }

  @Override
  public boolean isSecure()
  {
    return "https".equals(uriInfo.getRequestUri().getScheme());
  }

  @Override
  public String getAuthenticationScheme()
  {
    return SecurityContext.BASIC_AUTH;
  }
}