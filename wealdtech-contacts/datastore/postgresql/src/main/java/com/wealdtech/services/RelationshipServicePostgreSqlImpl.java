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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.handles.EmailHandle;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.contacts.handles.NameHandle;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.RelationshipService;
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
public class RelationshipServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Relationship> implements RelationshipService<PreparedStatement>
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
  public Relationship obtain(final WID<Relationship> relationshipId)
  {
    return Iterables.getFirst(obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
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
        setJson(stmt, index++, "{\"_id\":\"" + relationshipId.toString() + "\"}");
      }
    }), null);
  }

  @Nullable
  @Override
  public Relationship obtain(final WID<Contact> fromId, final WID<Contact> toId)
  {
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
        setJson(stmt, index++, "{\"from\":\"" + fromId.toString() + "\"}");
        setJson(stmt, index++, "{\"to\":\"" + toId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableList<Relationship> obtain(final WID<Contact> fromId,
                                            @Nullable final String name,
                                            @Nullable final String email,
                                            final Context context)
  {
    checkState(context != null, "Missing context");

    final ImmutableList.Builder<Relationship> resultsB = ImmutableList.builder();

    if (email != null)
    {
      // Try to obtain existing relationship
      final ImmutableList<Relationship> relationships = obtain(fromId, EmailHandle.builder().address(email).build());
      if (relationships.size() == 0)
      {
        // No relationships; nothing to do here
      }
      else if (relationships.size() == 1)
      {
        // Exactly one relationship so it's good
        final Relationship relationship = relationships.iterator().next();

//        Relationship relationship = obtain(fromId, contact.getId());
//        if (relationship == null)
//        {
//          // We don't have a relationship with this contact to date; set one up
//          relationship = Relationship.builder()
//                                     .from(fromId)
//                                     .to(contact.getId())
//                                     .contexts(ImmutableSet.of(
//                                         Context.builder().situation(situation).familiarity(50).formality(50).build()))
//                                     .build();
//          create(relationship);
//        }
//        Context context = relationship.obtainContext(context);
//        if (context == null)
//        {
//          // We don't have a relationship with this contact in this context; set one up
//          context = Context.builder().situation(situation).familiarity(50).formality(50).build();
//          relationship = Relationship.builder(relationship)
//                                     .contexts(
//                                         ImmutableSet.<Context>builder().addAll(relationship.getContexts()).add(context).build())
//                                     .build();
//          update(relationship);
//        }
//
        resultsB.add(relationship);
      }
      else // >1 contacts
      {
        // Multiple matches; try to trim them down based on name
      }
    }
    else if (name != null)
    {
      // Try to find given their name
      final ImmutableList<Relationship> relationships = obtain(fromId, NameHandle.builder().name(name).build());
      // For each one ensure that the name is suitable for this context
      for (final Relationship relationship : relationships)
      {
        for (final Use use : relationship.getUses())
        {
          if (use instanceof NameUse)
          {
            if (((NameUse)use).getName().equalsIgnoreCase(name) && use.getContext() == context)
            {
              resultsB.add(relationship);
            }
          }
        }
      }
    }
    return resultsB.build();
  }

  @Nullable
  @Override
  public Relationship match(final WID<Contact> fromId,
                            @Nullable final String name,
                            @Nullable final String email,
                            final Context context)
  {
    final ImmutableList<Relationship> potentials = obtain(fromId, name, email, context);
    Relationship bestMatch = null;
    int bestFamiliarity = 0;
    for (final Relationship potential : potentials)
    {
      for (final Use use : potential.getUses())
      {
        if (use instanceof NameUse)
        {
          if (((NameUse)use).getName().equalsIgnoreCase(name) && use.getContext() == context)
          {
            int thisFamiliarity = use.getFamiliarity();
            if (thisFamiliarity > bestFamiliarity)
            {
              bestFamiliarity = thisFamiliarity;
              bestMatch = potential;
            }
          }
        }
      }
    }
    return bestMatch;
  }

  @Override
  public ImmutableList<Relationship> obtain(final WID<Contact> fromId, final Handle handle)
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
        setJson(stmt, index++, "{\"from\":\"" + fromId.toString() + "\"}");
        setJson(stmt, index++, "{\"uses\":[{\"_key\":\"" + handle.getKey() + "\"}]}");
      }
    });

  }
  //  @Override
  //  public ImmutableList<Relationship> obtain(final String name, final Context.Situation situation)
  //  {
  //    return obtain(RELATIONSHIP_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
  //    {
  //      @Override
  //      public String getConditions()
  //      {
  //        return "d @> ?";
  //      }
  //
  //      @Override
  //      public void setConditionValues(final PreparedStatement stmt)
  //      {
  //        int index = 1;
  //        setJson(stmt, index++, "{\"contexts\":[{\"handles\":[\"" + name + "\"]}]}");
  //      }
  //    });
  //  }

  @Override
  public ImmutableList<Relationship> obtain()
  {
    return obtain(RELATIONSHIP_TYPE_REFERENCE, null);
  }

  @Override
  public void remove(final Relationship relationship)
  {
    super.remove(relationship.getId());
  }
}
