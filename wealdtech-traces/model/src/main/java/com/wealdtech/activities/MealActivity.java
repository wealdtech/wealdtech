/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.activities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A meal activity.
 */
@JsonTypeName("generic")
public class MealActivity extends Activity<MealActivity> implements Comparable<MealActivity>
{
  private static final String _TYPE = "meal";

  private static final String MEAL_TYPE = "mealtype";

  @JsonIgnore
  public MealType getMealType() { return get(MEAL_TYPE, MealType.class).get(); }

  @JsonCreator
  public MealActivity(final Map<String, Object> data){ super(data); }

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
  }

  public static class Builder<P extends Builder<P>> extends Activity.Builder<MealActivity, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final MealActivity prior)
    {
      super(prior);
    }

    public P mealType(final MealType mealType)
    {
      data(MEAL_TYPE, mealType);
      return self();
    }

    public MealActivity build()
    {
      return new MealActivity(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final MealActivity prior)
  {
    return new Builder(prior);
  }

  public static enum MealType
  {
    UNSPECIFIED,
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    OTHER;

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }

    @JsonCreator
    public static MealType fromString(final String type)
    {
      checkNotNull(type, "Meal type is required");
      try
      {
        return valueOf(type.trim().toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A meal type supplied is invalid"); // NOPMD
      }
    }
  }
}
