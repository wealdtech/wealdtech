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
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;

import javax.annotation.Nullable;

/**
 *
 */
public interface RelationshipService
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
   * Obtain a relationships for a given name and context situation
   */
  ImmutableList<Relationship> obtain(String name, @Nullable Context.Situation situation);

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
