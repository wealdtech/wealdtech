/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.WID;
import com.wealdtech.Application;
import com.wealdtech.chat.Subscription;
import com.wealdtech.chat.Topic;
import com.wealdtech.User;
import com.wealdtech.repositories.PostgreSqlRepository;
import com.wealdtech.services.WObjectServiceCallbackPostgreSqlImpl;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 * Subscription service using PostgreSQL as a backend
 */
public class SubscriptionServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Subscription> implements SubscriptionService
{
  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionServicePostgreSqlImpl.class);

  private static final TypeReference<Subscription> SUBSCRIPTION_TYPE_REFERENCE = new TypeReference<Subscription>(){};

  @Inject
  public SubscriptionServicePostgreSqlImpl(final PostgreSqlRepository repository,
                                           @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "subscription");
  }

  @Override
  public ImmutableSet<Subscription> obtain(final WID<Application> appId, final WID<Topic> topicId)
  {
    return ImmutableSet.copyOf(obtain(SUBSCRIPTION_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        setJson(stmt, 1, "{\"" + ChatDatastoreConstants.APP_ID + "\":\"" + appId.toString() + "\"}");
        setJson(stmt, 2, "{\"" + ChatDatastoreConstants.TOPIC_ID + "\":\"" + topicId.toString() + "\"}");
      }
    }));
  }

  @Override
  public ImmutableSet<Subscription> obtain(final WID<Application> appId, final WID<Topic> topicId, final ImmutableCollection<WID<User>> userIds)
  {
    return ImmutableSet.copyOf(obtain(SUBSCRIPTION_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ? AND jsonb_exists_any(d->'user', ?)";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        setJson(stmt, 1, "{\"" + ChatDatastoreConstants.APP_ID + "\":\"" + appId.toString() + "\"}");
        setJson(stmt, 2, "{\"" + ChatDatastoreConstants.TOPIC_ID + "\":\"" + topicId.toString() + "\"}");
        setWIDArray(stmt, 3, userIds);
      }
    }));
  }
}
