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
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.utils.StringUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * The type of a device: iOS, Android, etc.
 */
public enum DeviceType
{
  /**
   * iOS-based device
   */
  IOS(1)
  /**
   * Android-based device
   */
 ,ANDROID(2)
  ;

  public final int val;

  private DeviceType(final int val)
  {
    this.val = val;
  }

  private static final ImmutableSortedMap<Integer, DeviceType> _VALMAP;
  static
  {
    final Map<Integer, DeviceType> levelMap = Maps.newHashMap();
    for (final DeviceType deviceType : DeviceType.values())
    {
      levelMap.put(deviceType.val, deviceType);
    }
    _VALMAP = ImmutableSortedMap.copyOf(levelMap);
  }

  @Override
  @JsonValue
  public String toString()
  {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
  }

  @JsonCreator
  public static DeviceType fromString(final String val)
  {
    try
    {
      return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because
      // this happens during invocation, and in that case the enum handler
      // will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("An activity type \"" + val + "\" supplied is invalid");
    }
  }

  public static DeviceType fromInt(final Integer val)
  {
    checkNotNull(val, "Device type not supplied");
    final DeviceType state = _VALMAP.get(val);
    checkNotNull(state, "Device type is invalid");
    return state;
  }

  /**
   * A meta class to allow us to view the internal values of the enum
   */
  public static class Meta
  {
    public static final Meta IOS = new Meta(DeviceType.IOS);
    public static final Meta ANDROID = new Meta(DeviceType.ANDROID);

    public static final ImmutableCollection<Meta> DEVICE_TYPES =
        ImmutableSet.of(IOS, ANDROID);

    @JsonSerialize(using = MetaSerializer.class)
    private DeviceType type;

    private Meta(final DeviceType type)
    {
      this.type = type;
    }
  }

  public static class MetaSerializer extends JsonSerializer<DeviceType>
  {
    @Override
    public void serialize(final DeviceType value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException
    {
      jgen.writeStartObject();
      jgen.writeObjectField("name", value.toString());
      jgen.writeObjectField("val", value.val);
      jgen.writeEndObject();
    }
  }
}
