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
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 *
 */
public class RelationshipServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Relationship> implements RelationshipService
{
  private static final Logger LOG = LoggerFactory.getLogger(RelationshipServicePostgreSqlImpl.class);

  private static final TypeReference<Relationship> RELATIONSHIP_TYPE_REFERENCE = new TypeReference<Relationship>() {};

  @Inject
  public RelationshipServicePostgreSqlImpl(final PostgreSqlRepository repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "relationship");
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

  @Override
  public ImmutableList<Relationship> obtain(final String name, final Context.Situation situation)
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
        setJson(stmt, index++, "{\"contexts\":[{\"handles\":[\"" + name + "\"]}]}");
      }
    });
  }

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
