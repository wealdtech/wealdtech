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
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.Application;
import com.wealdtech.DeviceRegistration;
import com.wealdtech.User;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authorisation.UserAuthorisation;
import com.wealdtech.services.UserService;
import com.wealdtech.utils.RequestHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import static com.wealdtech.Preconditions.checkPermission;

/**
 * Resource for user device methods
 */
@Path("devices")
@Singleton
public class DeviceResource
{
  private static final Logger LOG = LoggerFactory.getLogger(DeviceResource.class);

  private final UserService userService;

  @Inject
  public DeviceResource(final UserService userService)
  {
    this.userService = userService;
  }

  /**
   * Register a device to a user
   */
  @Timed
  @POST
  public void registerDevice(@Context final Application application,
                             @Context final UserAuthorisation authorisation,
                             @Context final User user,
                             @Context final RequestHint hint,
                             final DeviceRegistration registration)
  {
    // Ensure that the user is allowed to register the device
    checkPermission(AuthorisationScope.canWrite(authorisation.getScope()), "Not allowed to register a device for that user");

    // See if the device is already registered
    for (final DeviceRegistration deviceRegistration : user.getDeviceRegistrations())
    {
      if (Objects.equal(registration, deviceRegistration))
      {
        // Already registered.  We don't fail because re-registration is fine; just silently ignore it
        return;
      }
    }

    // Carry out the update
    final User updatedUser = User.builder(user)
                                 .deviceRegistrations(ImmutableSet.<DeviceRegistration>builder()
                                                                  .addAll(user.getDeviceRegistrations())
                                                                  .add(registration)
                                                                  .build())
                                 .build();
    userService.update(user, updatedUser);
  }

  /**
   * Unregister a device from a user
   */
  @Timed
  @DELETE
  @Path("{deviceid}")
  public void unregisterDevice(@Context final Application application,
                               @Context final UserAuthorisation authorisation,
                               @Context final User user,
                               @Context final RequestHint hint,
                               @PathParam("deviceid") final String registrationId)
  {
    // Ensure that the user is allowed to unregister the device
    checkPermission(AuthorisationScope.canWrite(authorisation.getScope()), "Not allowed to unregister a device for that user");

    // Ensure the device is currently registered
    boolean registered = false;
    for (final DeviceRegistration deviceRegistration : user.getDeviceRegistrations())
    {
      if (Objects.equal(deviceRegistration.getDeviceId(), registrationId))
      {
        registered = true;
        break;
      }
    }
    if (!registered)
    {
      // An attempt to unregister an unknown device.  Common problem with clients; silently ignore it
      return;
    }

    final ImmutableSet<DeviceRegistration> updatedRegistrations =
        ImmutableSet.copyOf(Collections2.filter(user.getDeviceRegistrations(), new Predicate<DeviceRegistration>()
                                                {
                                                  @Override
                                                  public boolean apply(final DeviceRegistration input)
                                                  {
                                                    return input != null && !Objects.equal(input.getDeviceId(), registrationId);
                                                  }
                                                }));
    final User updatedUser = User.builder(user).deviceRegistrations(updatedRegistrations).build();
    userService.update(user, updatedUser);
  }

  //  /**
  //   * Add a user
  //   *
  //   * @param user the user to add
  //   */
  //  @Timed
  //  @POST
  //  @Consumes({MediaType.APPLICATION_JSON, UserMediaType.V1_JSON})
  //  public void createUser(@PathParam("appid") final WID<Application> appId,
  //                            final User user)
  //  {
  //    // Ensure that the application ID presented is valid
  //
  //    // Create the message
  //    userService.createMessage(appId, topicId, message);
  //  }
}
