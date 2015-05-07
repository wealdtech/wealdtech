/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.pushwoosh;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

/**
 */
public class PushWooshRequest extends WObject<PushWooshRequest>
{
  private static final String APPLICATION = "application";
  private static final String APPLICATIONS_GROUP = "applications_group";
  private static final String AUTH = "auth";
  private static final String NOTIFICATIONS = "notifications";

  @JsonCreator
  public PushWooshRequest(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    if (!exists(APPLICATION))
    {
      throw new DataError.Missing("PushWoosh request failed validation: requires application");
    }
    if (!exists(NOTIFICATIONS))
    {
      throw new DataError.Missing("PushWoosh request failed validation: requires notifications");
    }
  }

  @JsonIgnore
  public String getApplication() { return get(APPLICATION, String.class).get(); }

  @JsonIgnore
  public String getApplicationsGroup() { return get(APPLICATIONS_GROUP, String.class).get(); }

  @JsonIgnore
  public String getAuth() { return get(AUTH, String.class).get(); }

  private static final TypeReference<ImmutableSet<PushWooshNotification>> NOTIFICATIONS_TYPEREF = new TypeReference<ImmutableSet<PushWooshNotification>>(){};

  @JsonIgnore
  public ImmutableSet<PushWooshNotification> getNotifications() { return get(NOTIFICATIONS, NOTIFICATIONS_TYPEREF).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<PushWooshRequest, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final PushWooshRequest prior)
    {
      super(prior);
    }

    public P application(final String application)
    {
      data(APPLICATION, application);
      return self();
    }

    public P applicationsGroup(final String applicationsGroup)
    {
      data(APPLICATIONS_GROUP, applicationsGroup);
      return self();
    }

    public P auth(final String auth)
    {
      data(AUTH, auth);
      return self();
    }

    public P notifications(final ImmutableSet<PushWooshNotification> notifications)
    {
      data(NOTIFICATIONS, notifications);
      return self();
    }

    public PushWooshRequest build()
    {
      return new PushWooshRequest(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final PushWooshRequest prior)
  {
    return new Builder(prior);
  }
}
