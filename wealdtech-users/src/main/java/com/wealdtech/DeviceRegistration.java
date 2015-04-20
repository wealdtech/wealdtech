/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Registration for a mobile device
 */
public class DeviceRegistration extends WObject<DeviceRegistration> implements Comparable<DeviceRegistration>
{
  private static final String TYPE = "type";
  private static final String DEVICE_ID = "deviceid";

  @JsonCreator
  public DeviceRegistration(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    if (!exists(TYPE))
    {
      throw new DataError.Missing("Device registration needs 'type' information");
    }

    if (!exists(DEVICE_ID))
    {
      throw new DataError.Missing("Device registration needs 'deviceid' information");
    }
  }

  @JsonIgnore
  public DeviceType getType()
  {
    return get(TYPE, DeviceType.class).get();
  }

  @JsonIgnore
  public String getDeviceId(){ return get(DEVICE_ID, String.class).get(); }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<DeviceRegistration, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final DeviceRegistration prior)
    {
      super(prior);
    }

    public P type(final DeviceType type)
    {
      data(TYPE, type);
      return self();
    }

    public P deviceId(final String deviceId)
    {
      data(DEVICE_ID, deviceId);
      return self();
    }

    public DeviceRegistration build()
    {
      return new DeviceRegistration(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final DeviceRegistration prior)
  {
    return new Builder(prior);
  }
}
