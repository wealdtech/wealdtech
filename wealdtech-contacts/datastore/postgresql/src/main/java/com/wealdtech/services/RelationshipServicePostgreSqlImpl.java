/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.handles.EmailHandle;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.contacts.handles.NameHandle;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.contacts.uses.EmailUse;
import com.wealdtech.contacts.uses.NameUse;
import com.wealdtech.contacts.uses.Use;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;

import static com.wealdtech.Preconditions.checkState;

/**
 *
 */
public class RelationshipServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Relationship> implements
    RelationshipService<PreparedStatement>
{
  private static final Logger LOG = LoggerFactory.getLogger(RelationshipServicePostgreSqlImpl.class);

  private static final TypeReference<Relationship> RELATIONSHIP_TYPE_REFERENCE = new TypeReference<Relationship>() {};

  private final ContactService<?> contactService;

  @Inject
  public RelationshipServicePostgreSqlImpl(final PostgreSqlRepository repository,
                                           final ContactService contactService,
                                           @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "relationship");
    this.contactService = contactService;
  }

  @Override
  public void create(final Relationship relationship)
  {
    super.add(relationship);
  }

  @Override
  public Relationship obtain(final WID<User> ownerId, final WID<Relationship> relationshipId)
  {
    checkState(ownerId != null, "Missing owner ID");
    checkState(relationshipId != null, "Missing relationship ID");

    return Iterables.getFirst(obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
        setJson(stmt, index++, "{\"_id\":\"" + relationshipId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public Relationship obtainForContact(final WID<User> ownerId, final WID<Contact> contactId)
  {
    checkState(ownerId != null, "Missing owner ID");
    checkState(contactId != null, "Missing contact ID");

    return Iterables.getFirst(obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
        setJson(stmt, index++, "{\"to\":\"" + contactId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableList<Relationship> obtain(final WID<User> ownerId,
                                            final Context context,
                                            @Nullable final WID<Contact> contactId,
                                            @Nullable final String name,
                                            @Nullable final String email)
  {
    checkState(ownerId != null, "Missing owner ID");
    checkState(context != null, "Missing context");

    ImmutableList<Relationship> relationships = ImmutableList.of();

    // Start by building the handles and corresponding uses if we have been supplied with an email and/or name
    Handle emailHandle = null;
    EmailUse emailUse = null;
    if (email != null)
    {
      emailHandle = EmailHandle.builder().address(email).build();
      emailUse = (EmailUse)emailHandle.toUse(context, 1, 50);
    }

    Handle nameHandle = null;
    NameUse nameUse = null;
    if (name != null)
    {
      nameHandle = NameHandle.builder().name(name).build();
      nameUse = (NameUse)nameHandle.toUse(context, 1, 50);
    }

    // If we have been given the contact ID then we either find the contact or we don't
    if (contactId != null)
    {
      final Relationship relationship = obtainForContact(ownerId, contactId);
      if (relationship != null)
      {
        relationships = ImmutableList.of(relationship);
      }
    }
    else
    {
      if (emailHandle != null)
      {
        // Email is best handle
        relationships = obtain(ownerId, context, emailHandle);
      }
      else if (nameHandle != null)
      {
        // Name handle is next try
        relationships = obtain(ownerId, context, nameHandle);
      }
    }

    if (relationships.size() == 0)
    {
      // Nothing found
      return relationships;
    }
    else if (relationships.size() == 1)
    {
      // We obtained a single specific relationship.  If we have handles that we used to obtain it then increase familiarity, and if
      // not then we create them
      boolean foundEmailUse = false;
      boolean foundNameUse = false;

      final ImmutableSet.Builder<Use> updatedUsesB = ImmutableSet.builder();
      for (final Use use : relationships.iterator().next().getUses())
      {
        if (emailUse != null && Objects.equal(use.getKey(), emailUse.getKey()))
        {
          updatedUsesB.add(use.increaseFamiliarity());
          foundEmailUse = true;
        }
        else if (nameUse != null && Objects.equal(use.getKey(), nameUse.getKey()))
        {
          updatedUsesB.add(use.increaseFamiliarity());
          foundNameUse = true;
        }
        else
        {
          updatedUsesB.add(use);
        }
      }
      if (!foundEmailUse && emailUse != null)
      {
        // Email use is new
        updatedUsesB.add(emailUse);
      }
      if (!foundNameUse && nameUse != null)
      {
        // Name use is new
        updatedUsesB.add(nameUse);
      }
      final Relationship updatedRelationship =
          Relationship.builder(relationships.iterator().next()).uses(updatedUsesB.build()).build();

      if (!Objects.equal(updatedRelationship, relationships.iterator().next())) { update(updatedRelationship); }

      return ImmutableList.of(updatedRelationship);
    }
    else
    {
      // We have multiple relationships.  Try to trim them down based on the familiarity of use for each handle
      if (nameHandle != null)
      {
        relationships = trimRelationshipsByHandle(relationships, nameUse);
      }
      if (emailHandle != null)
      {
        relationships = trimRelationshipsByHandle(relationships, emailUse);
      }
      return relationships;
    }
  }

  private ImmutableList<Relationship> trimRelationshipsByHandle(ImmutableList<Relationship> relationships, Use use)
  {
    ImmutableList.Builder<Relationship> bestMatchesB = ImmutableList.builder();
    int bestFamiliarity = 0;
    for (final Relationship potential : relationships)
    {
      for (final Use potentialUse : potential.getUses())
      {
        if (Objects.equal(potentialUse.getKey(), use.getKey()))
        {
          int thisFamiliarity = potentialUse.getFamiliarity();
          if (thisFamiliarity > bestFamiliarity)
          {
            bestFamiliarity = thisFamiliarity;
            bestMatchesB = ImmutableList.builder();
            bestMatchesB.add(potential);
            break;
          }
          else if (thisFamiliarity == bestFamiliarity)
          {
            bestMatchesB.add(potential);
          }
        }
      }
    }
    return bestMatchesB.build();
  }

  @Override
  public ImmutableList<Relationship> obtain(final WID<User> ownerId, final Context context, final Handle handle)
  {
    return obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
        setJson(stmt, index++, "{\"uses\":[{\"_key\":\"" + context.toString().toLowerCase() + "::" + handle.getKey() + "\"}]}");
      }
    });
  }

  @Override
  public ImmutableList<Relationship> obtain(final WID<User> ownerId)
  {
    return obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
      }
    });
  }

  @Override
  public void remove(final Relationship relationship)
  {
    super.remove(relationship.getId());
  }
}
