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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Gravatar handle
 */
@JsonTypeName("gravatar")
public class GravatarHandle extends SiteHandle<GravatarHandle> implements Comparable<GravatarHandle>
{
  private static final String _TYPE = "gravatar";

  private static final Pattern ACCOUNT_URL = Pattern.compile("^https?://(www\\.)?gravatar\\.com/([^/?]+)");

  public GravatarHandle(final Map<String, Object> data)
  {
    super(data);
  }

  /**
   * See if a URL matches an account URL
   */
  public static boolean matchesAccountUrl(@Nullable String url)
  {
    return url != null && ACCOUNT_URL.matcher(url).matches();
  }

  /**
   * Obtain the local ID of the user given their account URL
   * @param url the account URL
   * @return the local ID of the user
   */
  @Nullable
  public static String localIdFromAccountUrl(@Nullable final String url)
  {
    if (url == null) { return null; }
    final Matcher matcher = ACCOUNT_URL.matcher(url);
    return matcher.find() ? matcher.group(2) : null;
  }

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

    return super.preCreate(data);
  }

  public static class Builder<P extends Builder<P>> extends SiteHandle.Builder<GravatarHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GravatarHandle prior)
    {
      super(prior);
    }

    public GravatarHandle build()
    {
      return new GravatarHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GravatarHandle prior)
  {
    return new Builder(prior);
  }
}
