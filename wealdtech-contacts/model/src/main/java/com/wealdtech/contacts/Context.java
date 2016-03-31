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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * A definition of the context of a relationship.
 */
public class Context extends WObject<Context> implements Comparable<Context>
{
  private static final String TYPE = "type";
  private static final String KNOWN_AS = "knownas";
  private static final String FAMILIARITY = "familiarity";
  private static final String FORMALITY = "formality";

  @JsonCreator
  public Context(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public ContextType getType() { return get(TYPE, ContextType.class).get(); }

  private static final TypeReference<ImmutableSet<String>> KNOWN_AS_TYPE_REF = new TypeReference<ImmutableSet<String>>(){};
  @JsonIgnore
  public ImmutableSet<String> getKnownAs() { return get(KNOWN_AS, KNOWN_AS_TYPE_REF).or(ImmutableSet.<String>of()); }

  @JsonIgnore
  public int getFamiliarity() { return get(FAMILIARITY, Integer.class).get(); }

  @JsonIgnore
  public int getFormality() { return get(FORMALITY, Integer.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(TYPE))
    {
      throw new DataError.Missing("Context needs 'type' information");
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

    public P type(final ContextType type)
    {
      data(TYPE, type);
      return self();
    }

    public P knownAs(final ImmutableSet<String> knownAs)
    {
      data(KNOWN_AS, knownAs);
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
