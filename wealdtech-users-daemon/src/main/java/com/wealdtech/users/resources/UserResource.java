/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.users.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.services.UserService;
import com.wealdtech.utils.RequestHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Resource for user methods
 */
@Path("users")
@Singleton
public class UserResource
{
  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private final UserService userService;

  @Inject
  public UserResource(final UserService userService)
  {
    this.userService = userService;
  }

  /**
   * Obtain a single user
   */
  @Timed
  @GET
  @Path("{userid: [A-Za-z0-9]+}")
  @Produces({MediaType.APPLICATION_JSON, UserMediaType.V1_JSON})
  public User getUser(@Context final UserAuthorisation authorisation,
                      @Context final RequestHint hint,
                      @Context final User user,
                      @PathParam("userid") final WID<User> userId)
  {
    // We already have the user so just return them
    return user;
  }

  /**
   * Add a user
   *
   * @param user the user to add
   */
  @Timed
  @POST
  @Consumes({MediaType.APPLICATION_JSON, UserMediaType.V1_JSON})
  public void createUser(final User user)
  {
    // Ensure that the application ID presented is valid

    // Create the message
    userService.create(user);
  }
}
