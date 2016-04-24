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
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.WObject;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A definition of the context of a relationship.
 */
public class Context extends WObject<Context> implements Comparable<Context>
{
  private static final String SITUATION = "situation";
  private static final String HANDLES = "handles";
  private static final String FAMILIARITY = "familiarity";
  private static final String FORMALITY = "formality";

  @JsonCreator
  public Context(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Situation getSituation() { return get(SITUATION, Situation.class).get(); }

  private static final TypeReference<ImmutableSet<String>> HANDLES_TYPE_REF = new TypeReference<ImmutableSet<String>>(){};

  /**
   * @return the handles specific to this relationship's context
   */
  @JsonIgnore
  public ImmutableSet<String> getHandles() { return get(HANDLES, HANDLES_TYPE_REF).or(ImmutableSet.<String>of()); }

  /**
   * @return the level of familiarity in this relationship's context
   */
  @JsonIgnore
  public int getFamiliarity() { return get(FAMILIARITY, Integer.class).get(); }

  /**
   * @return the level of formality in this relationship's context
   */
  @JsonIgnore
  public int getFormality() { return get(FORMALITY, Integer.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(SITUATION))
    {
      throw new DataError.Missing("Context failed validation: needs 'situation'");
    }
  }

  /**
   * The situation of a context: professional, social, familial
   */
  public static enum Situation
  {
    /**
     * Professional relationship
     */
    PROFESSIONAL(1)
    /**
     * Social relationship
     */
    , SOCIAL(2)
    /**
     * Familial relationship
     */
    , FAMILIAL(3);

    public final int val;

    private Situation(final int val)
    {
      this.val = val;
    }

    private static final ImmutableSortedMap<Integer, Situation> _VALMAP;

    static
    {
      final Map<Integer, Situation> levelMap = Maps.newHashMap();
      for (final Situation relationshipType : Situation.values())
      {
        levelMap.put(relationshipType.val, relationshipType);
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
    public static Situation fromString(final String val)
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
        throw new DataError.Bad("A situation \"" + val + "\" supplied is invalid");
      }
    }

    public static Situation fromInt(final Integer val)
    {
      checkNotNull(val, "Situation not supplied");
      final Situation state = _VALMAP.get(val);
      checkNotNull(state, "Situation is invalid");
      return state;
    }
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Context, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Context prior)
    {
      super(prior);
    }

    public P situation(final Situation situation)
    {
      data(SITUATION, situation);
      return self();
    }

    public P knownAs(final ImmutableSet<String> knownAs)
    {
      data(HANDLES, knownAs);
      return self();
    }

    public P familiarity(final int familiarity)
    {
      data(FAMILIARITY, familiarity);
      return self();
    }

    public P formality(final int formality)
    {
      data(FORMALITY, formality);
      return self();
    }

    public Context build()
    {
      return new Context(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Context prior)
  {
    return new Builder(prior);
  }

}
