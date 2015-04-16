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
import com.wealdtech.DataError;
import com.wealdtech.WID;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A chat element
 */
public class Message extends ChatObject<Message> implements Comparable<Message>
{
  private static final Logger LOG = LoggerFactory.getLogger(Message.class);

  private static final String FROM = "from";
  private static final String SCOPE = "scope";
  private static final String TIMESTAMP = "timestamp";
  private static final String TO = "to";
  private static final String TOPIC = "topic";
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
      data.put(TIMESTAMP, new DateTime());
    }
    return data;
  }

  protected void validate()
  {
    super.validate();
    if (!exists(FROM))
    {
      throw new DataError.Missing("Chat needs 'from' information");
    }

    if (!exists(SCOPE))
    {
      throw new DataError.Missing("Chat needs 'scope' information");
    }

    if (!exists(TIMESTAMP))
    {
      throw new DataError.Missing("Chat needs 'timestamp' information");
    }

    final MessageScope scope = getScope();
    if (scope == MessageScope.FRIENDS || scope == MessageScope.INDIVIDUAL)
    {
      if (!exists(TO))
      {
        throw new DataError.Missing("Directed chat needs 'to' information");
      }
      final ImmutableSet<WID<User>> to = getTo();
      if (to.isEmpty())
      {
        throw new DataError.Missing("Directed chat needs 'to' information");
      }
    }

    if (!exists(TOPIC))
    {
      throw new DataError.Missing("Chat needs 'topic' information");
    }

    if (!exists(TEXT))
    {
      throw new DataError.Missing("Chat needs 'text' information");
    }
  }

  @JsonIgnore
  public String getFrom()
  {
    return get(FROM, String.class).get();
  }

  @JsonIgnore
  public MessageScope getScope()
  {
    return get(SCOPE, MessageScope.class).get();
  }

  @JsonIgnore
  public DateTime getTimestamp()
  {
    return get(TIMESTAMP, DateTime.class).get();
  }

  private static final TypeReference<ImmutableSet<WID<User>>> TO_TYPE_REF = new TypeReference<ImmutableSet<WID<User>>>(){};
  @JsonIgnore
  public ImmutableSet<WID<User>> getTo()
  {
    return get(TO, TO_TYPE_REF).or(ImmutableSet.<WID<User>>of());
  }

  @JsonIgnore
  public String getTopic() { return get(TOPIC, String.class).get(); }

  @JsonIgnore
  public String getText()
  {
    return get(TEXT, String.class).get();
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

    public P from(final String from)
    {
      data(FROM, from);
      return self();
    }

    public P scope(final MessageScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public P timestamp(final DateTime timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P to(final ImmutableSet<WID<User>> to)
    {
      data(TO, to);
      return self();
    }

    public P topic(final String topic)
    {
      data(TOPIC, topic);
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
