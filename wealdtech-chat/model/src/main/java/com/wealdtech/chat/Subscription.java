/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
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
import com.wealdtech.DataError;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A subscription to a topic.
 * A single user can only be subscribed to a topic once.
 */
public class Subscription extends ChatObject<Subscription> implements Comparable<Subscription>
{
  private static final Logger LOG = LoggerFactory.getLogger(Subscription.class);

  private static final String USER = "user";
  private static final String TOPIC = "topic";

  @JsonCreator
  public Subscription(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    super.validate();
    if (!exists(USER))
    {
      throw new DataError.Missing("Subscription needs 'user' information");
    }

    if (!exists(TOPIC))
    {
      throw new DataError.Missing("Subscription needs 'topic' information");
    }
  }

  @JsonIgnore
  public String getUser()
  {
    return get(USER, String.class).get();
  }

  @JsonIgnore
  public String getTopic()
  {
    return get(TOPIC, String.class).get();
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Subscription, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Subscription prior)
    {
      super(prior);
    }

    public P user(final String user)
    {
      data(USER, user);
      return self();
    }

    public P topic(final String topic)
    {
      data(TOPIC, topic);
      return self();
    }

    public Subscription build()
    {
      return new Subscription(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Subscription prior)
  {
    return new Builder(prior);
  }
}
