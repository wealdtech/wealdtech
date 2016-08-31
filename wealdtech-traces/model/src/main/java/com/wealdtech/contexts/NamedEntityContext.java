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
 * A Context for a generic named entity.
 */
@JsonTypeName("person")
public class NamedEntityContext extends Context<NamedEntityContext> implements Comparable<NamedEntityContext>
{
  private static final String _TYPE = "namedentity";

  private static final String NAME = "name";
  private static final String GENDER = "gender";

  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }

  @JsonIgnore
  public Gender getGender() { return get(GENDER, Gender.class).get(); }

  @JsonCreator
  public NamedEntityContext(final Map<String, Object> data){ super(data); }

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

    checkState(exists(NAME), "Named entity context failed validation: missing name");
    checkState(exists(GENDER), "Named entity context failed validation: missing gender");
  }

  public static class Builder<P extends Builder<P>> extends Context.Builder<NamedEntityContext, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final NamedEntityContext prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P gender(final Gender gender)
    {
      data(GENDER, gender);
      return self();
    }

    public NamedEntityContext build()
    {
      return new NamedEntityContext(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final NamedEntityContext prior)
  {
    return new Builder(prior);
  }

  public static enum Gender
  {
    UNSPECIFIED,
    MALE,
    FEMALE,
    NEUTER,
    OTHER;

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }

    @JsonCreator
    public static Gender fromString(final String type)
    {
      checkNotNull(type, "Gender is required");
      try
      {
        return valueOf(type.trim().toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A gender supplied is invalid"); // NOPMD
      }
    }
  }
}
