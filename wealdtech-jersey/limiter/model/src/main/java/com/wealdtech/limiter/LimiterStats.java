/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.limiter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Statistics used by the limiter
 */
public class LimiterStats extends WObject<LimiterStats>
{
  private static final String TIMESTAMP = "timestamp";
  private static final String KEY = "key";
  private static final String REQUESTS = "requests";

  @JsonCreator
  public LimiterStats(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(TIMESTAMP), "Limiter stats failed validation: requires timestamp");
    checkState(exists(KEY), "Limiter stats failed validation: requires key");
    checkState(exists(REQUESTS), "Limiter stats failed validation: requires requests");
  }

  @JsonIgnore
  public Long getTimestamp() { return get(TIMESTAMP, Long.class).get(); }

  @JsonIgnore
  public String getKey() { return get(KEY, String.class).get(); }

  @JsonIgnore
  public Long getRequests() { return get(REQUESTS, Long.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<LimiterStats, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final LimiterStats prior)
    {
      super(prior);
    }

    public P timestamp(final Long timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P key(final String key)
    {
      data(KEY, key);
      return self();
    }

    public P requests(final Long requests)
    {
      data(REQUESTS, requests);
      return self();
    }

    public LimiterStats build()
    {
      return new LimiterStats(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final LimiterStats prior)
  {
    return new Builder(prior);
  }
}
