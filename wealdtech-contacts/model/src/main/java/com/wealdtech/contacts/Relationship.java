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
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.contacts.uses.Use;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A unidirectional relationship from one contact to another.
 * The relationship is different depending on the context, so this just holds a number of contexts
 */
public class Relationship extends WObject<Relationship> implements Comparable<Relationship>
{
  private static final String FROM = "from";
  private static final String TO = "to";
  private static final String USES = "uses";

  @JsonCreator
  public Relationship(final Map<String, Object> data)
  {
    super(data);
  }

  private static final TypeReference<WID<Contact>> FROM_TYPE_REF = new TypeReference<WID<Contact>>(){};
  @JsonIgnore
  public WID<Contact> getFrom() { return get(FROM, FROM_TYPE_REF).get(); }

  private static final TypeReference<WID<Contact>> TO_TYPE_REF = new TypeReference<WID<Contact>>(){};
  @JsonIgnore
  public WID<Contact> getTo() { return get(TO, TO_TYPE_REF).get(); }

  private static final TypeReference<ImmutableSet<? extends Use>> USES_TYPE_REF = new TypeReference<ImmutableSet<? extends Use>>(){};
  @JsonIgnore
  public ImmutableSet<? extends Use> getUses() { return get(USES, USES_TYPE_REF).or(ImmutableSet.<Use>of()); }

//  @JsonIgnore
//  @Nullable
//  public ImmutableList<? extends Use> obtainUses(final Context.Situation situation)
//  {
//    for (final Use use : getUses())
//    {
//      if (Objects.equal(situation, context.getSituation()))
//      {
//        return context;
//      }
//    }
//    return null;
//  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(FROM), "Relationship failed validation: must contain from");
    checkState(exists(TO), "Relationship failed validation: must contain to");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Relationship, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Relationship prior)
    {
      super(prior);
    }

    public P from(final WID<Contact> from)
    {
      data(FROM, from);
      return self();
    }

    public P to(final WID<Contact> to)
    {
      data(TO, to);
      return self();
    }

    public P uses(final ImmutableSet<? extends Use> uses)
    {
      data(USES, uses);
      return self();
    }

    public Relationship build()
    {
      return new Relationship(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Relationship prior)
  {
    return new Builder(prior);
  }
}
