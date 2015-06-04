/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authentication;

import com.wealdtech.ServerError;
import com.wealdtech.User;

import static com.wealdtech.Preconditions.checkState;

/**
 * Authenticate a user
 */
public class UserAuthenticator
{
  private final User user;

  public UserAuthenticator(final User user)
  {
    this.user = user;
  }

  public AuthorisationScope authenticate(final Credentials credentials)
  {
    checkState(credentials != null, "Credentials are required to authenticate a user");

    // Obtain the correct authenticator given the credentials
    String type = credentials.getType();
    if (!type.contains("."))
    {
      // Type is unqualified - add standard qualifier
      type = "com.wealdtech.authentication." + type + "Authenticator";
    }

    try
    {
      final Class<?> klazz = Class.forName(type);
      final Authenticator authenticator = (Authenticator)klazz.newInstance();
      return authenticator.authenticate(credentials, user.getAuthenticationMethods());
    }
    catch (final ClassNotFoundException e)
    {
      throw new ServerError("Unknown authenticator " + type);
    }
    catch (final InstantiationException e)
    {
      throw new ServerError("Unable to instantiate authenticator " + type);
    }
    catch (final IllegalAccessException e)
    {
      throw new ServerError("Unable to access authenticator " + type);
    }
  }
}
