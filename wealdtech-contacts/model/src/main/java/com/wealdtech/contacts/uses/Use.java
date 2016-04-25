/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.uses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Objects;
import com.wealdtech.RangedWObject;
import com.wealdtech.contacts.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A Use contains the details of how something is used to communicate with a contact.
 * This is the abstract superclass; details of specific uses are available in the subclasses.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(com.wealdtech.contacts.uses.NameUse.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.uses.EmailUse.class)})
public abstract class Use<T extends Use<T>> extends RangedWObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(Use.class);

  // The type is used when deserializing
  protected static final String TYPE = "type";
  // The key uniquely defines the use
  protected static final String KEY = "_key";
  protected static final String CONTEXT = "context";
  private static final String FAMILIARITY = "familiarity";
  private static final String FORMALITY = "formality";

  @JsonCreator
  public Use(final Map<String, Object> data){ super(data); }

  /**
   * @return the type of the use.  This allows for correct serialization/deserialization
   */
  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  /**
   * @return the key for the use.  The key uniquely identifies the use
   */
  @JsonIgnore
  public String getKey(){ return get(KEY, String.class).get(); }

  /**
   * @return the context to which this use applies
   */
  @JsonIgnore
  public Context getContext() { return get(CONTEXT, Context.class).get(); }

  /**
   * @return the level of familiarity in this use.  When multiple uses are available the one with the highest familiarity should be chosen
   */
  @JsonIgnore
  public int getFamiliarity() { return get(FAMILIARITY, Integer.class).get(); }

  /**
   * @return the level of formality in this use.  A formality of 0 implies complete informality, and a formality of 100 implies complete formality
   */
  @JsonIgnore
  public int getFormality() { return get(FORMALITY, Integer.class).get(); }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    // Prepend context and type to the key and ensure it is lower-case
    data.put(KEY, (data.get(CONTEXT) + "::" + data.get(TYPE) + "::" + data.get(KEY)).toLowerCase(Locale.ENGLISH));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TYPE), "Use failed validation: must contain type");
    checkState(exists(KEY), "Use failed validation: must contain key");
    checkState(Objects.equal(getKey(), getKey().toLowerCase()), "Use failed validation: key must be lower-case");
    checkState(exists(CONTEXT), "Use failed validation: must contain context");
    checkState(exists(FORMALITY), "Use failed validation: must contain formality");
    checkState(exists(FAMILIARITY), "Use failed validation: must contain familiarity");
  }

  public static class Builder<T extends Use<T>, P extends Builder<T, P>> extends RangedWObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P type(final String type)
    {
      data(TYPE, type);
      return self();
    }

    public P key(final String key)
    {
      data(KEY, key);
      return self();
    }

    public P context(final Context context)
    {
      data(CONTEXT, context);
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
  }
}