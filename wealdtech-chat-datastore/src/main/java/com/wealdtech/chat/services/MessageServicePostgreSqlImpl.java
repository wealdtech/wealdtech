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
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.chat.Message;
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

  private static final TypeReference<Message> CHAT_TYPE_REFERENCE = new TypeReference<Message>(){};

  @Inject
  public MessageServicePostgreSqlImpl(final PostgreSqlRepository repository,
                                      @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "message");
  }

  @Override
  public ImmutableList<Message> getChats(final String from, @Nullable final String topic)
  {
    return obtain(CHAT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return topic == null ? null : "d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"topic\":\"" + topic + "\"}");
      }
    });
  }
}
