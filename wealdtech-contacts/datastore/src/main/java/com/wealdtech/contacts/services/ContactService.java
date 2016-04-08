/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.services;

import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;

/**
 *
 */
public interface ContactService
{
  /**
   * Create a contact
   */
  void create(Contact contact);

  /**
   * Obtain a contact
   */
  Contact obtain(WID<Contact> contactId);

  /**
   * Update a contact
   */
  void update(Contact contact);

  /**
   * Remove a contact
   */
  void remove(Contact contact);
}
