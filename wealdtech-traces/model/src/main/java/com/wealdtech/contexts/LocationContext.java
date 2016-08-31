/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contexts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * A Context for a location.
 */
@JsonTypeName("location")
public class LocationContext extends Context<LocationContext> implements Comparable<LocationContext>
{
  private static final String _TYPE = "location";

  private static final String NAME = "name";
  private static final String LOCATION_TYPE = "locationtype";

  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }

  @JsonCreator
  public LocationContext(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Set our defining types
    data.put(TYPE, _TYPE);

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();

    checkState(exists(NAME), "Person context failed validation: missing name");
  }

  public static class Builder<P extends Builder<P>> extends Context.Builder<LocationContext, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final LocationContext prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }
    public P locationType(final Type type)
    {
      data(LOCATION_TYPE, type);
      return self();
    }

    public LocationContext build()
    {
      return new LocationContext(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final LocationContext prior)
  {
    return new Builder(prior);
  }

  public static enum Type
  {
    UNSPECIFIED,
    RESTAURANT,
    WORKPLACE,
    APARTMENT,
    OTHER;

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }

    @JsonCreator
    public static Type fromString(final String type)
    {
      checkNotNull(type, "Gender type is required");
      try
      {
        return valueOf(type.trim().toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A location type supplied is invalid"); // NOPMD
      }
    }
  }
}
