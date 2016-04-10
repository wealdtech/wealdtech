/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.google.common.collect.ImmutableList;
import com.wealdtech.authentication.Credentials;

import javax.annotation.Nullable;

/**
 *
 */
public interface ContactsClient<C extends Credentials>
{
  /**
   * Obtain a contact of the primary user
   *
   * @param credentials credentials for the request
   * @param contactId the ID of the contact to obtain
   * @return the obtained contact, or {@code null} if no such contact
   */
  @Nullable
  Contact obtainContact(C credentials, String contactId);

  /**
   * Obtain a contact of a specific user
   *
   * @param credentials credentials for the request
   * @param email the email of the user for which to obtain the contact
   * @param contactId the ID of the contact to obtain
   * @return the obtained contact, or {@code null} if no such contact
   */
  @Nullable
  Contact obtainContact(C credentials, String email, String contactId);

  /**
   * Obtain all contacts of the primary user
   *
   * @param credentials credentials for the request
   * @return the obtained contacts
   */
  ImmutableList<Contact> obtainContacts(C credentials);

  /**
   * Obtain all contacts of a specific user
   *
   * @param credentials credentials for the request
   * @param email the email of the user for which to obtain the contacts
   * @return the obtained contacts
   */
  ImmutableList<Contact> obtainContacts(C credentials, String email);
}
