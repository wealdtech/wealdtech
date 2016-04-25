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
import com.google.common.base.Optional;
import com.wealdtech.DataError;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;

import java.util.Locale;
import java.util.Map;

/**
 * SiteHandle contains the handle for a generic site.
 */
public abstract class SiteHandle<T extends SiteHandle<T>> extends Handle<T> implements Comparable<T>
{
  private static final String LOCAL_ID = "localid";
  private static final String NAME = "name";

  @JsonCreator
  public SiteHandle(final Map<String, Object> data){ super(data); }

  /**
   * @return the local ID for the contact on the site
   */
  @JsonIgnore
  public String getLocalId() { return get(LOCAL_ID, String.class).get(); }

  /**
   * @return the name for the user on the site
   */
  @JsonIgnore
  public Optional<String> getName() { return get(NAME, String.class); }

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
    if (data.containsKey(LOCAL_ID))
    {
      data.put(KEY, ((String)data.get(LOCAL_ID)).toLowerCase(Locale.ENGLISH));
    }

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();

    if (!exists(LOCAL_ID))
    {
      throw new DataError.Missing("Site handle failed validation: missing local id");
    }
  }

  public static class Builder<T extends SiteHandle<T>, P extends Builder<T, P>> extends Handle.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P localId(final String localId)
    {
      data(LOCAL_ID, localId);
      return self();
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }
  }
}
