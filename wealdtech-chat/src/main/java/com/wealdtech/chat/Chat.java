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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A chat element
 */
public class Chat extends WObject<Chat> implements Comparable<Chat>
{
  private static final Logger LOG = LoggerFactory.getLogger(Chat.class);

  private static final String FROM = "from";
  private static final String SCOPE = "scope";
  private static final String TIMESTAMP = "timestamp";
  private static final String TO = "to";
  private static final String TOPIC = "topic";
  private static final String MESSAGE = "message";

  public Chat(@JsonProperty("data") final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
//    if (!exists(FROM))
//    {
//      throw new DataError.Missing("Chat needs 'from' information");
//    }
//
//    if (!exists(SCOPE))
//    {
//      throw new DataError.Missing("Chat needs 'scope' information");
//    }
//
//    if (!exists(TIMESTAMP))
//    {
//      set(TIMESTAMP, new DateTime().getMillis());
//    }
//
//    final ChatScope scope = getScope();
//    if (scope == ChatScope.GROUP || scope == ChatScope.INDIVIDUAL)
//    {
//      if (!exists(TO))
//      {
//        throw new DataError.Missing("Directed chat needs 'to' information");
//      }
//      final ImmutableSet<String> to = getTo();
//      if (to.isEmpty())
//      {
//        throw new DataError.Missing("Directed chat needs 'to' information");
//      }
//    }
//
//    if (!exists(TOPIC))
//    {
//      throw new DataError.Missing("Chat needs 'topic' information");
//    }
//
//    if (!exists(MESSAGE))
//    {
//      throw new DataError.Missing("Chat needs 'message' information");
//    }
  }

  @JsonIgnore
  public String getFrom()
  {
    return get(FROM, String.class);
  }

  @JsonIgnore
  public ChatScope getScope()
  {
    return get(SCOPE, ChatScope.class);
  }

  @JsonIgnore
  public DateTime getTimestamp()
  {
    return new DateTime(get(TIMESTAMP, Long.class));
  }

  private static final TypeReference<ImmutableSet<String>> TO_TYPE_REF = new TypeReference<ImmutableSet<String>>(){};
  @JsonIgnore
  public ImmutableSet<String> getTo()
  {
    return get(TO, TO_TYPE_REF);
  }

  @JsonIgnore
  public String getTopic() { return get(TOPIC, String.class); }

  @JsonIgnore
  public String getMessage()
  {
    return get(MESSAGE, String.class);
  }

  public static class Builder<T extends Builder<T>> extends WObject.Builder<T>
  {
    public T from(final String from)
    {
      data(FROM, from);
      return self();
    }

    public T scope(final ChatScope scope)
    {
      data(SCOPE, scope);
      return self();
    }

    public T timestamp(final DateTime timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public T to(final ImmutableSet<String> to)
    {
      data(TO, to);
      return self();
    }

    public T topic(final String topic)
    {
      data(TOPIC, topic);
      return self();
    }

    public T message(final String message)
    {
      data(MESSAGE, message);
      return self();
    }

    public Chat build()
    {
      return new Chat(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }
}
