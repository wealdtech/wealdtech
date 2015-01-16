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
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.chat.Chat;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
import com.wealdtech.services.WObjectServiceCallbackPostgreSqlImpl;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;

/**
 * Chat service using PostgreSQL as a backend
 */
public class ChatServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Chat> implements ChatService
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatServicePostgreSqlImpl.class);

  private static final TypeReference<Chat> CHAT_TYPE_REFERENCE = new TypeReference<Chat>(){};

  @Inject
  public ChatServicePostgreSqlImpl(final PostgreSqlRepository repository)
  {
    super(repository, "chat");
  }

  @Override
  public ImmutableList<Chat> getChats(final String from, @Nullable final String topic)
  {
    return obtain(CHAT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "f_data->>'topic'=?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setString(stmt, index++, topic);
      }
    });
  }
}
