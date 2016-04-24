/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.GenericWObject;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.Credentials;

import javax.annotation.Nullable;

/**
 */
public interface UserService<T> extends WObjectService<User, T>
{
  /**
   * Create a user.
   * @param user the user to create
   */
  void create(User user);

  /**
   * Obtain a user given a set of credentials.  Note that even though it is passed credentials it does not carry out any
   * authentication.  The one exception to this is if the credentials passed in relate to a one-time token, in which case the
   * authentication is implicit.  In this case the token is removed to avoid it being used multiple times
   * @param credentials the credentials used to obtain the user
   * @return the user; can be {@code null}
   * @throws com.wealdtech.DataError.Authentication if the authentication fails
   */
  User obtain(Credentials credentials);

  /**
   * Obtain a user given a user ID.
   * @param id the ID of the user to be obtained
   * @return the user; can be {@code null} if no user with that ID exists
   */
  @Nullable
  User obtain(WID<User> id);

  /**
   * Obtain a set of users given their IDs
   * @param ids the IDs of the users to be obtained
   * @return the users; can be empty if no users with the provided IDs exist
   */
  ImmutableSet<User> obtain(ImmutableCollection<WID<User>> ids);

  /**
   * Obtain a user given an email address.  The email address match is case-insensitive
   * @param emailAddress the email address of the user to be obtained
   * @return the user; can be {@code null} if no user with that email address exists
   */
  @Nullable
  User obtain(String emailAddress);

  /**
   * Obtain all users.
   * @return a set containing all users
   */
  ImmutableSet<User> obtainAll();

  /**
   * Remove a user
   * @param user the user to remove
   */
  void remove(User user);

  /**
   * Update a user
   * @param oldUser the user as currently exists in the datastore
   * @param newUser the user as should be updated in the datastore
   */
  void update(User oldUser, User newUser);

  /**
   * Verify an email address
   *
   * @param credentials
   */
  void verifyEmail(Credentials credentials);

  /**
   * Obtain emails known by the system given a set of emails provided.
   * This is used to check for duplicate email addresses, as well as
   * a lookup against mobile clients' address books
   * @param emails the emails to check
   * @return the subset of the emails passed in which match existing users plus the their ID
   */
  ImmutableSet<GenericWObject> obtainKnownEmails(ImmutableCollection<String> emails);
}
