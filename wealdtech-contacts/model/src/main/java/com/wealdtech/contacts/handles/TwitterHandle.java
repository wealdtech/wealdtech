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

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Twitter handle
 */
@JsonTypeName("twitter")
public class TwitterHandle extends SiteHandle<TwitterHandle> implements Comparable<TwitterHandle>
{
  private static final String _TYPE = "twitter";

  public TwitterHandle(final Map<String, Object> data)
  {
    super(data);
  }

  private static final Pattern ACCOUNT_URL = Pattern.compile("^https?://(www\\.)?twitter\\.com/([^/?]+)");

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    data.put(TYPE, _TYPE);

    return super.preCreate(data);
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

  public static class Builder<P extends Builder<P>> extends SiteHandle.Builder<TwitterHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final TwitterHandle prior)
    {
      super(prior);
    }

    public TwitterHandle build()
    {
      return new TwitterHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final TwitterHandle prior)
  {
    return new Builder(prior);
  }
}
