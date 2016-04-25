/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.handles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * A handle that defines a contact's telephone number.
 */
@JsonTypeName("telephone")
public class TelephoneHandle extends Handle<TelephoneHandle> implements Comparable<TelephoneHandle>
{
  private static final String _TYPE = "telephone";

  private static final String NUMBER = "number";
  private static final String DEVICE = "device";

  @JsonCreator
  public TelephoneHandle(final Map<String, Object> data){ super(data); }

  /**
   * @return The number of the telephone for the contact
   */
  @JsonIgnore
  public String getNumber() { return get(NUMBER, String.class).get(); }

  /**
   * @return the (optional) device of the telephone for the contact
   */
  @JsonIgnore
  public Optional<Device> getDevice() { return get(DEVICE, Device.class); }

  @Override
  public boolean hasUse()
  {
    return false;
  }

  @Override
  public Use toUse(final Context context, final int familiarity, final int formality)
  {
    return null;
  }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, ((String)data.get(NUMBER)).replaceAll("[^0-9]", ""));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NUMBER), "Telephone handle failed validation: must contain number");
  }

  public static enum Device
  {
    LANDLINE(1)

    ,MOBILE(2)

    ,FAX(3)

    ,PAGER(4)
    ;

    private static final ImmutableSortedMap<Integer, Device> _VALMAP;

    static
    {
      final Map<Integer, Device> levelMap = Maps.newHashMap();
      for (final Device relationshipType : Device.values())
      {
        levelMap.put(relationshipType.val, relationshipType);
      }
      _VALMAP = ImmutableSortedMap.copyOf(levelMap);
    }

    public final int val;
    private Device(final int val)
    {
      this.val = val;
    }

    @JsonCreator
    public static Device fromString(final String val)
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
        throw new DataError.Bad("A telephone X \"" + val + "\" supplied is invalid");
      }
    }

    public static Device fromInt(final Integer val)
    {
      checkNotNull(val, "Telephone X not supplied");
      final Device state = _VALMAP.get(val);
      checkNotNull(state, "Telephone X is invalid");
      return state;
    }

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }
  }

  public static class Builder<P extends Builder<P>> extends Handle.Builder<TelephoneHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final TelephoneHandle prior)
    {
      super(prior);
    }

    public P number(final String number)
    {
      data(NUMBER, number);
      return self();
    }

    public P device(final Device device)
    {
      data(DEVICE, device);
      return self();
    }

    public TelephoneHandle build()
    {
      return new TelephoneHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }
  
  public static Builder<?> builder(final TelephoneHandle prior)
  {
    return new Builder(prior);
  }

}
