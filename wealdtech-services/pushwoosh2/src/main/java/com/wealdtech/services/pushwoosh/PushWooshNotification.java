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
public class PushWooshNotification extends WObject<PushWooshNotification>
{
  private static final String SEND_DATE = "send_date";
  private static final String IGNORE_USER_TIMEZONE = "ignore_user_timezone";
  private static final String DATA = "data";
  private static final String DEVICES = "devices";

  @JsonCreator
  public PushWooshNotification(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    if (!exists(SEND_DATE))
    {
      throw new DataError.Missing("PushWoosh notification failed validation: requires send date");
    }
    if (!exists(IGNORE_USER_TIMEZONE))
    {
      throw new DataError.Missing("PushWoosh notification failed validation: requires ignore user timezone flag");
    }
    if (!exists(DATA))
    {
      throw new DataError.Missing("PushWoosh notification failed validation: requires data");
    }
    if (!exists(DEVICES))
    {
      throw new DataError.Missing("PushWoosh notification failed validation: requires devices");
    }
  }

  @JsonIgnore
  public String getSendDate() { return get(SEND_DATE, String.class).get(); }

  @JsonIgnore
  public Boolean isIgnoreUserTimezone() { return get(IGNORE_USER_TIMEZONE, Boolean.class).get(); }

  @JsonIgnore
  public WObject<?> getPushWooshData() { return get(DATA, WObject.class).get(); }

  private static final TypeReference<ImmutableSet<String>> DEVICES_TYPEREF = new TypeReference<ImmutableSet<String>>(){};

  @JsonIgnore
  public ImmutableSet<String> devices() { return get(DEVICES, DEVICES_TYPEREF).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<PushWooshNotification, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final PushWooshNotification prior)
    {
      super(prior);
    }

    public P sendDate(final String sendDate)
    {
      data(SEND_DATE, sendDate);
      return self();
    }

    public P ignoreUserTimezone(final Boolean ignoreUserTimezone)
    {
      data(IGNORE_USER_TIMEZONE, ignoreUserTimezone);
      return self();
    }

    public P data(final WObject<?> data)
    {
      data(DATA, data);
      return self();
    }

    public P devices(final ImmutableSet<String> devices)
    {
      data(DEVICES, devices);
      return self();
    }

    public PushWooshNotification build()
    {
      return new PushWooshNotification(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final PushWooshNotification prior)
  {
    return new Builder(prior);
  }
}
