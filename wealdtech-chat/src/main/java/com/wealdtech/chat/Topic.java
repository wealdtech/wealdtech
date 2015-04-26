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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.DataError;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A chat topic.  The topic defines a container for a set of messages.
 * Each topic has a set of owner IDs and participant IDs.
 */
public class Topic extends ChatObject<Topic> implements Comparable<Topic>
{
  private static final Logger LOG = LoggerFactory.getLogger(Topic.class);

  private static final String NAME = "name";
  private static final String OWNER_IDS = "ownerids";
  private static final String PARTICIPANT_IDS = "participantids";

  @JsonCreator
  public Topic(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    super.validate();
    if (!exists(NAME))
    {
      throw new DataError.Missing("Topic needs 'name' information");
    }

    if (!exists(OWNER_IDS))
    {
      throw new DataError.Missing("Topic needs 'ownerids' information");
    }

    if (!exists(PARTICIPANT_IDS))
    {
      throw new DataError.Missing("Topic needs 'userids' information");
    }
  }

  private static final TypeReference<ImmutableSet<WID<User>>> OWNER_IDS_TYPEREF = new TypeReference<ImmutableSet<WID<User>>>() {};

  @JsonIgnore
  public ImmutableSet<WID<User>> getOwnerIds()
  {
    return get(OWNER_IDS, OWNER_IDS_TYPEREF).get();
  }

  private static final TypeReference<ImmutableSet<WID<User>>> PARTICIPANT_IDS_TYPEREF = new TypeReference<ImmutableSet<WID<User>>>() {};

  @JsonIgnore
  public ImmutableSet<WID<User>> getParticipantIds()
  {
    return get(PARTICIPANT_IDS, PARTICIPANT_IDS_TYPEREF).get();
  }

  @JsonIgnore
  public String getName()
  {
    return get(NAME, String.class).get();
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Topic, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Topic prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P participantIds(final ImmutableSet<WID<User>> participantIds)
    {
      data(PARTICIPANT_IDS, participantIds);
      return self();
    }

    public P ownerIds(final ImmutableSet<WID<User>> ownerIds)
    {
      data(OWNER_IDS, ownerIds);
      return self();
    }

    public Topic build()
    {
      return new Topic(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Topic prior)
  {
    return new Builder(prior);
  }
}
