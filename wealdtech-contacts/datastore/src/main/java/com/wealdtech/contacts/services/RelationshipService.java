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
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.services.WObjectService;

import javax.annotation.Nullable;

/**
 *
 */
public interface RelationshipService<T> extends WObjectService<Relationship, T>
{
  /**
   * Create a relationship
   */
  void create(Relationship relationship);

  /**
   * Obtain a relationship
   *
   * @param ownerId the ID of the user obtaining the relationship
   * @param relationshipId the ID of the relationship
   *
   * @return the relationship; can be {@code null}
   */
  @Nullable Relationship obtain(WID<User> ownerId, WID<Relationship> relationshipId);

  /**
   * Obtain a relationship for a contact
   *
   * @param ownerId the ID of the user obtaining the relationship
   * @param contactId the ID of the contact
   *
   * @return the relationship; can be {@code null}
   */
  @Nullable Relationship obtainForContact(WID<User> ownerId, WID<Contact> contactId);

  /**
   * Obtain the best matching relationships given some relationship information.
   * This will usually return a single relationship, however in the case where there are multiple matching relationships with no
   * differentiation then multiple relationships will be returned.
   *
   * @param ownerId the ID of the user obtaining the relationships
   * @param contactId the ID of the contact
   * @param name the name of the contact
   * @param email the email address of the contact
   *
   * @return a list of the best matching relationships; can be empty
   */
  ImmutableList<Relationship> obtain(WID<User> ownerId,
                                     Context context, @Nullable WID<Contact> contactId, @Nullable String name,
                                     @Nullable String email);

  /**
   * Obtain all relationships with a given handle
   *
   * @param ownerId the ID of the user obtaining the relationships
   * @param context the context of the relationship
   * @param handle a handle for the relationship
   *
   * @return a list of the matching relationships; can be empty
   */
  ImmutableList<Relationship> obtain(WID<User> ownerId, final Context context, Handle handle);

  /**
   * Obtain all relationships
   *
   * @param ownerId the ID of the user obtaining the relationships
   *
   * @return all relationships known by this user
   */
  ImmutableList<Relationship> obtain(WID<User> ownerId);

  /**
   * Update a relationship
   *
   * @param relationship the relationship to update
   */
  void update(Relationship relationship);

  /**
   * Remove a relationship
   *
   * @param relationship the relationship to remove
   */
  void remove(Relationship relationship);
}
