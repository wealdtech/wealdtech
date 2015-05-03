/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.Application;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.datastore.repositories.PostgreSqlRepository;
import com.wealdtech.services.WObjectServiceCallbackPostgreSqlImpl;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;

/**
 * Message service using PostgreSQL as a backend
 */
public class MessageServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Message> implements MessageService
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageServicePostgreSqlImpl.class);

  private static final TypeReference<Message> MESSAGE_TYPE_REFERENCE = new TypeReference<Message>() {};

  @Inject
  public MessageServicePostgreSqlImpl(final PostgreSqlRepository repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "message");
  }

  @Override
  public void create(final WID<Application> appId, final WID<Topic> topicId, final Message message)
  {
    // Add the application Id and topic ID to the message before creating it
    final Message messageToCreate =
        Message.builder(message).data(ChatDatastoreConstants.APP_ID, appId).data(ChatDatastoreConstants.TOPIC_ID, topicId).build();
    super.add(messageToCreate);
  }

  @Override
  public ImmutableList<Message> obtain(final WID<Application> appId, final WID<Topic> topicId)
  {
    return obtain(MESSAGE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
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
        setJson(stmt, index++, "{\"" + ChatDatastoreConstants.TOPIC_ID + "\":\"" + topicId.toString() + "\"}");
      }
    });
  }

  @Override
  @Nullable
  public Message obtain(final WID<Application> appId, final WID<Topic> topicId, final WID<Message> messageId)
  {
    return Iterables.getFirst(obtain(MESSAGE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"" + ChatDatastoreConstants.APP_ID + "\":\"" + appId.toString() + "\"}");
        setJson(stmt, index++, "{\"" + ChatDatastoreConstants.TOPIC_ID + "\":\"" + topicId.toString() + "\"}");
        setJson(stmt, index++, "{\"_id\":\"" + messageId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableList<Message> obtainFrom(final WID<Application> appId, final WID<Topic> topicId, final WID<User> userId)
  {
    return obtain(MESSAGE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"appid\":\"" + appId.toString() + "\"}");
        setJson(stmt, index++, "{\"topicid\":\"" + topicId.toString() + "\"}");
        setJson(stmt, index++, "{\"from\":\"" + userId.toString() + "\"}");
      }
    });
  }

  @Override
  public ImmutableList<Message> obtainTo(final WID<Application> appId, final WID<Topic> topicId, final WID<User> userId)
  {
    return obtain(MESSAGE_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ? AND (d @> '{\"scope\":\"Everyone\"}' OR d @> ?)";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"appid\":\"" + appId.toString() + "\"}");
        setJson(stmt, index++, "{\"topicid\":\"" + topicId.toString() + "\"}");
        setJson(stmt, index++, "{\"to\":[\"" + userId.toString() + "\"]}");
      }
    });
  }
}
