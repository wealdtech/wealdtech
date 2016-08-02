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
import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Participant;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.uses.Use;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 *
 */
public class ParticipantServiceSynchronousImpl implements ParticipantService
{
  private final RelationshipService<?> relationshipService;

  @Inject
  public ParticipantServiceSynchronousImpl(final RelationshipService<?> relationshipService)
  {
    this.relationshipService = relationshipService;
  }

  @Override
  public ImmutableSet<Participant> obtain(final WID<User> ownerId,
                                          final Context context,
                                          @Nullable final WID<Contact> contactId,
                                          @Nullable final String name,
                                          @Nullable final String email)
  {
    final ImmutableList<Relationship> relationships = relationshipService.obtain(ownerId, context, contactId, name, email);
    // Turn the relationships into participants
    final ImmutableSet.Builder<Participant> resultsB = ImmutableSet.builder();
    for (final Relationship relationship : relationships)
    {
      final Participant.Builder<?> participantB = Participant.builder();
      participantB.relationshipId(relationship.getId());
      final ImmutableSet.Builder<Use> usesB = ImmutableSet.builder();
      for (final Use use : relationship.getUses())
      {
        if (use.getContext() == context)
        {
          usesB.add(use);
        }
      }
      participantB.uses(usesB.build());
      resultsB.add(participantB.build());
    }

    return resultsB.build();
  }
}
