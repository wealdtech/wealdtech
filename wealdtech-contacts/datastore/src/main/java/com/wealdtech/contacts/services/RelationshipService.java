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
   */
  @Nullable Relationship obtain(WID<Relationship> relationshipId);

  /**
   * Obtain a relationship
   */
  @Nullable Relationship obtain(WID<Contact> fromId, WID<Contact> toId);

  /**
   * Obtain all matching relationships given some relationship information
   * @return a list of participants.  If this is 0 participants then it means that we could not find a match given the information supplied.  If this is 1 participant then it means that we found an exact match given the information supplied.  If this is more than 1 participant then it means that we found multiple potential matches given the information and need the user to provide us more information to narrow it down.
   */
  ImmutableList<Relationship> obtain(WID<Contact> fromId, @Nullable String name, @Nullable String email, Context context);

  /**
   * obtain best matching relationship given some relationship information
   * @return the best matching relationship, or {@code null} if there is no relationship
   */
  @Nullable Relationship match(WID<Contact> fromId, @Nullable String name, @Nullable String email, Context social);

  /**
   * Obtain all relationships with a given handle
   */
  ImmutableList<Relationship> obtain(WID<Contact> fromId, Handle handle);

  /**
   * Obtain all relationships
   */
  ImmutableList<Relationship> obtain();

  /**
   * Update a relationship
   */
  void update(Relationship relationship);

  /**
   * Remove a relationship
   */
  void remove(Relationship relationship);
}
