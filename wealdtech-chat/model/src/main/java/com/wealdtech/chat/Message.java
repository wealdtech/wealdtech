/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A chat message.  The chat message contains the message itself as well as its associated metadata
 */
public class Message extends ChatObject<Message> implements Comparable<Message>
{
  private static final Logger LOG = LoggerFactory.getLogger(Message.class);

  private static final String FROM_ID = "fromid";
  private static final String SCOPE = "scope";
  private static final String TIMESTAMP = "timestamp";
  private static final String TO_IDS = "toids";
  private static final String TEXT = "text";

  @JsonCreator
  public Message(final Map<String, Object> data)
  {
    super(data);
  }

  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);
    if (!data.containsKey(TIMESTAMP))
    {
      data.put(TIMESTAMP, new LocalDateTime());
    }
    return data;
  }

  protected void validate()
  {
    super.validate();

    checkState(exists(FROM_ID), "Message failed validation: must contain fromid");
    checkState(exists(SCOPE), "Message failed validation: must contain scope");
    checkState(exists(TIMESTAMP), "Message failed validation: must contain timestamp");

    final MessageScope scope = getScope();
    if (scope == MessageScope.GROUP || scope == MessageScope.INDIVIDUAL)
    {
      checkState(exists(TO_IDS) && !getTo().isEmpty(), "Message failed validation: directed message must contain to");
    }

    checkState(exists(TEXT), "Message failed validation: must contain text");
  }

  private static final TypeReference<WID<User>> FROM_TYPE_REF = new TypeReference<WID<User>>(){};
  @JsonIgnore
  public WID<User> getFrom()
  {
    return get(FROM_ID, FROM_TYPE_REF).get();
  }

  @JsonIgnore
  public MessageScope getScope()
  {
    return get(SCOPE, MessageScope.class).get();
  }

  @JsonIgnore
  public LocalDateTime getTimestamp()
  {
    return get(TIMESTAMP, LocalDateTime.class).get();
  }

  private static final TypeReference<ImmutableSet<WID<User>>> TO_TYPE_REF = new TypeReference<ImmutableSet<WID<User>>>(){};
  @JsonIgnore
  public ImmutableSet<WID<User>> getTo()
  {
    return get(TO_IDS, TO_TYPE_REF).or(ImmutableSet.<WID<User>>of());
  }

  @JsonIgnore
  public String getText()
  {
    return get(TEXT, String.class).get();
  }

  @Override
  public void onPriorToStore()
  {
    // Ensure that our timestamp is a localdatetime and not a string
    getTimestamp();
  }

  public static class Builder<P extends Builder<P>> extends ChatObject.Builder<Message, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Message prior)
    {
      super(prior);
    }

    public P fromId(final WID<User> fromId)
    {
      data(FROM_ID, fromId);
      return self();
    }

    public P scope(final MessageScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public P timestamp(final LocalDateTime timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P toIds(final ImmutableSet<WID<User>> toIds)
    {
      data(TO_IDS, toIds);
      return self();
    }

    public P text(final String text)
    {
      data(TEXT, text);
      return self();
    }

    public Message build()
    {
      return new Message(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Message prior)
  {
    return new Builder(prior);
  }
}
