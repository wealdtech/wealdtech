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
import com.wealdtech.DataError;

import java.util.Map;

/**
 * A handle related to a website.
 */
@JsonTypeName("website")
public class WebsiteHandle extends Handle<WebsiteHandle> implements Comparable<WebsiteHandle>
{
  private static final String _TYPE = "website";

  private static final String PATH = "path";
  private static final String LOCAL_ID = "localid";

  private static final String NAME = "name";

  @JsonIgnore
  public String getPath() { return get(PATH, String.class).get(); }

  @JsonIgnore
  public String getLocalId() { return get(LOCAL_ID, String.class).get(); }

  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }

  @JsonCreator
  public WebsiteHandle(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(LOCAL_ID) + "@" + data.get(PATH));

    return data;
  }

  @Override
  protected void validate()
  {
    super.validate();

    if (!exists(PATH))
    {
      throw new DataError.Missing("Website handle failed validation: missing PATH");
    }

    if (!exists(LOCAL_ID))
    {
      throw new DataError.Missing("Website handle failed validation: missing local id");
    }

    if (!exists(NAME))
    {
      throw new DataError.Missing("Website handle failed validation: missing name");
    }
  }

  public static class Builder<P extends Builder<P>> extends Handle.Builder<WebsiteHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final WebsiteHandle prior)
    {
      super(prior);
    }

    public P path(final String path)
    {
      data(PATH, path);
      return self();
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

    public WebsiteHandle build()
    {
      return new WebsiteHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final WebsiteHandle prior)
  {
    return new Builder(prior);
  }

}
