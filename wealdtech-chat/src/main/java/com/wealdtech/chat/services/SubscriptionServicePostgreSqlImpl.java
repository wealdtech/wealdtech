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
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.ServerError;
import com.wealdtech.chat.Subscription;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
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
  public SubscriptionServicePostgreSqlImpl(final PostgreSqlRepository repository)
  {
    super(repository, "subscription");
  }

  @Override
  public ImmutableList<Subscription> obtainForTopicAndUsers(final String topic, final ImmutableCollection<String> users)
  {
    return obtain(SUBSCRIPTION_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND jsonb_exists_any(d->'user', ?)";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        setJson(stmt, 1, "{\"topic\":\"" + topic + "\"}");
        setStringArray(stmt, 2, users);
      }
    });
  }

  @Override
  public ImmutableList<Subscription> obtainForTopic(final String topic)
  {
    return obtain(SUBSCRIPTION_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        setJson(stmt, 1, "{\"topic\":\"" + topic + "\"}");
      }
    });
  }
}
