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
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.ServerError;
import com.wealdtech.WID;
import com.wealdtech.Application;
import com.wealdtech.chat.Topic;
import com.wealdtech.datastore.repositories.PostgreSqlRepository;
import com.wealdtech.services.WObjectServiceCallbackPostgreSqlImpl;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;

/**
 * Topic service using PostgreSQL as a backend
 */
public class TopicServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Topic> implements TopicService
{
  private static final Logger LOG = LoggerFactory.getLogger(TopicServicePostgreSqlImpl.class);

  private static final TypeReference<Topic> TOPIC_TYPE_REFERENCE = new TypeReference<Topic>() {};

  @Inject
  public TopicServicePostgreSqlImpl(final PostgreSqlRepository repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "topic");
  }

  @Override
  public void create(final WID<Application> appId, final Topic topic)
  {
    // Add the application ID to the topic before creating it
    final Topic topicToCreate = Topic.builder(topic).data(ChatDatastoreConstants.APP_ID, appId).build();
    super.add(topicToCreate);
  }

  @Override
  public void update(final WID<Application> appId, final Topic topic)
  {
    throw new ServerError("Not implemented");
  }

  @Override
  @Nullable
  public Topic obtain(final WID<Application> appId, final WID<Topic> topicId)
  {
    return Iterables.getFirst(obtain(TOPIC_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
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
        setJson(stmt, index++, "{\"" + ChatDatastoreConstants.APP_ID + "\":\"" + appId.toString() + "\"}");
        setJson(stmt, index++, "{\"_id\":\"" + topicId.toString() + "\"}");
      }
    }), null);
  }
}
