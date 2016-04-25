/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.contacts.events.Event;
import com.wealdtech.contacts.handles.Handle;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static com.wealdtech.Preconditions.checkState;

/**
 * A contact contains details about a person.
 */
public class Contact extends WObject<Contact> implements Comparable<Contact>
{
  private static final String REMOTE_IDS = "remoteids";

  private static final String OWNER_ID = "ownerid";
  private static final String HANDLES = "handles";
  private static final String EVENTS = "events";

  @JsonCreator
  public Contact(final Map<String, Object> data)
  {
    super(data);
  }

  private static final TypeReference<Set<RemoteId>> REMOTE_IDS_TYPE_REF = new TypeReference<Set<RemoteId>>(){};
  @JsonIgnore
  public Set<RemoteId> getRemoteIds() { return get(REMOTE_IDS, REMOTE_IDS_TYPE_REF).or(Sets.<RemoteId>newHashSet()); }

  @JsonIgnore
  @Nullable public String obtainRemoteId(final String remoteService)
  {
    for (final RemoteId remoteId : getRemoteIds())
    {
      if (Objects.equal(remoteService, remoteId.getService()))
      {
        return remoteId.getRemoteId();
      }
    }
    return null;
  }

  private static final TypeReference<WID<User>> OWNER_ID_TYPE_REF = new TypeReference<WID<User>>(){};
  @JsonIgnore
  public WID<User> getOwnerId() { return get(OWNER_ID, OWNER_ID_TYPE_REF).get(); }

  private static final TypeReference<Set<? extends Handle>> HANDLES_TYPE_REF = new TypeReference<Set<? extends Handle>>(){};
  @JsonIgnore
  public Set<? extends Handle> getHandles() { return get(HANDLES, HANDLES_TYPE_REF).or(Sets.<Handle>newHashSet()); }

  private static final TypeReference<Set<? extends Event>> EVENTS_TYPE_REF = new TypeReference<Set<? extends Event>>(){};
  @JsonIgnore
  public Set<? extends Event> getEvents() { return get(EVENTS, EVENTS_TYPE_REF).or(Sets.<Event>newHashSet()); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(OWNER_ID), "Contact failed validation: missing owner ID");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Contact, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Contact prior)
    {
      super(prior);
    }

    public P remoteIds(final Set<RemoteId> remoteIds)
    {
      data(REMOTE_IDS, remoteIds);
      return self();
    }

    public P handles(final Set<? extends Handle> handles)
    {
      data(HANDLES, handles);
      return self();
    }

    public P events(final Set<? extends Event> events)
    {
      data(EVENTS, events);
      return self();
    }

    public P ownerId(final WID<User> ownerId)
    {
      data (OWNER_ID, ownerId);
      return self();
    }

    public Contact build()
    {
      return new Contact(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Contact prior)
  {
    return new Builder(prior);
  }

}