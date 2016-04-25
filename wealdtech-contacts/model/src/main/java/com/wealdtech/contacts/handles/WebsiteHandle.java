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
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A handle related to a generic website.
 */
@JsonTypeName("website")
public class WebsiteHandle extends Handle<WebsiteHandle> implements Comparable<WebsiteHandle>
{
  private static final String _TYPE = "website";

  private static final String URL = "url";

  @JsonCreator
  public WebsiteHandle(final Map<String, Object> data){ super(data); }

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
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(URL));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(URL), "Website handle failed validation: must contain URL");
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

    public P url(final String url)
    {
      data(URL, url);
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
