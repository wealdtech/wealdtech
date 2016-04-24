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
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A remote ID; basically a pointer to another system.
 */
public class RemoteId extends WObject<RemoteId> implements Comparable<RemoteId>
{
  private static final String SERVICE = "service";
  private static final String REMOTE_ID = "remoteid";

  @JsonCreator
  public RemoteId(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public String getService() { return get(SERVICE, String.class).get(); }

  @JsonIgnore
  public String getRemoteId() { return get(REMOTE_ID, String.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(SERVICE), "RemoteId failed validation: missing 'service'");
    checkState(exists(REMOTE_ID), "RemoteId failed validation: missing 'remote id'");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<RemoteId, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final RemoteId prior)
    {
      super(prior);
    }

    public P service(final String service)
    {
      data(SERVICE, service);
      return self();
    }

    public P remoteId(final String remoteId)
    {
      data(REMOTE_ID, remoteId);
      return self();
    }

    public RemoteId build()
    {
      return new RemoteId(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final RemoteId prior)
  {
    return new Builder(prior);
  }

}