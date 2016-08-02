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

import com.google.common.collect.ImmutableList;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.services.WObjectService;

import javax.annotation.Nullable;

/**
 *
 */
public interface ContactService<T> extends WObjectService<Contact, T>
{
  /**
   * Create a contact
   * @param contact the contact to create
   */
  void create(Contact contact);

  /**
   * Obtain a contact
   * @param ownerId the ID of the user obtaining the contact
   * @param contactId the ID of the contact to obtain
   * @return the contact
   */
  @Nullable Contact obtain(WID<User> ownerId, WID<Contact> contactId);

  /**
   * Obtain all contacts
   * @param ownerId the ID of the user obtaining the contacts
   * @return all contacts known by the user
   */
  ImmutableList<Contact> obtain(WID<User> ownerId);

  /**
   * Obtain all contacts with a given handle
   * @param ownerId the ID of the user obtaining the contacts
   * @param handle a handle of the user obtaining the contacts
   * @return all contacts known by the user that match the handle
   */
  ImmutableList<Contact> obtain(WID<User> ownerId, Handle handle);

  /**
   * Update a contact
   * @param contact the contact to update
   */
  void update(Contact contact);

  /**
   * Remove a contact
   * @param contact the contact to remove
   */
  void remove(Contact contact);
}
