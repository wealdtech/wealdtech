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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.contacts.uses.Use;

import javax.annotation.Nullable;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A unidirectional relationship from one contact to another.
 * The relationship is different depending on the context, so this just holds a number of contexts
 */
public class Relationship extends WObject<Relationship> implements Comparable<Relationship>
{
  private static final String OWNER_ID = "ownerid";
  private static final String TO = "to";
  private static final String USES = "uses";

  @JsonCreator
  public Relationship(final Map<String, Object> data)
  {
    super(data);
  }

  private static final TypeReference<WID<User>> OWNER_ID_TYPE_REF = new TypeReference<WID<User>>(){};
  @JsonIgnore
  public WID<User> getOwnerId() { return get(OWNER_ID, OWNER_ID_TYPE_REF).get(); }

  private static final TypeReference<WID<Contact>> TO_TYPE_REF = new TypeReference<WID<Contact>>(){};
  @JsonIgnore
  public WID<Contact> getTo() { return get(TO, TO_TYPE_REF).get(); }

  private static final TypeReference<ImmutableSet<? extends Use>> USES_TYPE_REF = new TypeReference<ImmutableSet<? extends Use>>(){};
  @JsonIgnore
  public ImmutableSet<? extends Use> getUses() { return get(USES, USES_TYPE_REF).or(ImmutableSet.<Use>of()); }

  /**
   * Obtain the best use of a given type
   * @param context the context in which the use applies
   * @return the best use; can be {@code null}
   */
  @Nullable
  public <T extends Use> T obtainBestUse(final Class<? extends Use> klazz, final Context context)
  {
    T bestUse = null;
    for (final Use use : getUses())
    {
      if (Objects.equal(context, use.getContext()) && Objects.equal(use.getClass(), klazz))
      {
        if (bestUse == null || use.getFamiliarity() > bestUse.getFamiliarity())
        {
          bestUse = (T)use;
        }
      }
    }
    return bestUse;
  }

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
    checkState(exists(OWNER_ID), "Relationship failed validation: must contain from");
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

    public P ownerId(final WID<User> ownerId)
    {
      data(OWNER_ID, ownerId);
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
