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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.wealdtech.DataError;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.utils.MapComparator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A chat element
 */
public class Chat implements Comparable<Chat>
{
  private static final Logger LOG = LoggerFactory.getLogger(Chat.class);

  private final String from;
  private final ChatScope scope;
  private final Long timestamp;
  private final ImmutableSet<String> to;
  private final String topic;
  private final String message;
  @JsonIgnore
  private final Map<String, Object> extensions;

  @JsonCreator
  public Chat(@JsonProperty("from") final String from,
              @JsonProperty("scope") final ChatScope scope,
              @JsonProperty("timestamp") final Long timestamp,
              @JsonProperty("to") final ImmutableSet<String> to,
              @JsonProperty("topic") final String topic,
              @JsonProperty("message") final String message,
              @JsonProperty("extensions") final Map<String, Object> extensions)
  {
    this.from = from;
    if (this.from == null)
    {
      throw new DataError.Missing("Chat needs 'from' information");
    }

    this.scope = scope;
    if (this.scope == null)
    {
      throw new DataError.Missing("Chat needs 'scope' information");
    }
    this.timestamp = MoreObjects.firstNonNull(timestamp, new DateTime().getMillis());

    this.to = MoreObjects.firstNonNull(to, ImmutableSet.<String>of());
    if ((this.scope == ChatScope.GROUP || this.scope == ChatScope.INDIVIDUAL) && this.to.isEmpty())
    {
      throw new DataError.Missing("Directed chat needs 'to' information");
    }

    this.topic = topic;
    if (topic == null)
    {
      throw new DataError.Missing("Chat needs 'topic' information");
    }

    this.message = message;
    if (this.message == null)
    {
      throw new DataError.Missing("Chat needs 'message' information");
    }

    this.extensions = MoreObjects.firstNonNull(extensions, Maps.<String, Object>newHashMap());

  }

  public String getFrom()
  {
    return from;
  }

  public ChatScope getScope()
  {
    return scope;
  }

  public Long getTimestamp()
  {
    return timestamp;
  }

  public ImmutableSet<String> getTo()
  {
    return to;
  }

  public String getTopic() { return this.topic; }

  public String getMessage()
  {
    return message;
  }

  @JsonIgnore
  public <T> T getExtension(final String name)
  {
    return (T)extensions.get(name);
  }

  @JsonAnyGetter
  private Map<String, Object> any()
  {
    return extensions;
  }

  @JsonAnySetter
  private void set(final String name, final Object value)
  {
    extensions.put(name, value);
  }

  public static class Builder
  {
    private String from;
    private ChatScope scope;
    private Long timestamp;
    private ImmutableSet<String> to;
    private String topic;
    private String message;
    private Map<String, Object> extensions;

    public Builder from(final String from)
    {
      this.from = from;
      return this;
    }

    public Builder scope(final ChatScope scope)
    {
      this.scope = scope;
      return this;
    }

    public Builder timestamp(final Long timestamp)
    {
      this.timestamp = timestamp;
      return this;
    }

    public Builder to(final ImmutableSet<String> to)
    {
      this.to = to;
      return this;
    }

    public Builder topic(final String topic)
    {
      this.topic = topic;
      return this;
    }

    public Builder message(final String message)
    {
      this.message = message;
      return this;
    }

    public Builder extensions(final Map<String, Object> extensions)
    {
      this.extensions = extensions;
      return this;
    }

    public Chat build()
    {
      return new Chat(from, scope, timestamp, to, topic, message, extensions);
    }
  }

  public static Builder builder()
  {
    return new Builder();
  }

  // Standard object methods follow
  @Override
  public String toString()
  {
    try
    {
      return WealdMapper.getMapper().writeValueAsString(this);
    }
    catch (final JsonProcessingException e)
    {
      LOG.error("Failed to create JSON for object: ", e);
      return "Bad";
    }
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.from, this.scope, this.timestamp, this.to, this.topic, this.message, this.extensions);
  }

  @Override
  public boolean equals(final Object that)
  {
    return that instanceof Chat && this.compareTo((Chat)that) == 0;
  }

  private static final MapComparator<String, Object> MAP_COMPARATOR = new MapComparator<>();

  @Override
  public int compareTo(@Nonnull final Chat that)
  {
    return ComparisonChain.start()
               .compare(this.from, that.from)
               .compare(this.scope, that.scope)
               .compare(this.timestamp, that.timestamp)
               .compare(this.to, that.to, Ordering.<String>natural().lexicographical())
               .compare(this.topic, that.topic)
               .compare(this.message, that.message)
        .compare(this.extensions, that.extensions, MAP_COMPARATOR)
        .result();
  }

}
