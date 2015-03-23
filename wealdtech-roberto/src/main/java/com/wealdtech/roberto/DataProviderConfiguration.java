/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * A generic object to contain configuration for data providers
 */
public class DataProviderConfiguration extends WObject<DataProviderConfiguration>
{
  @JsonIgnore
  private static final String UPDATE_INTERVAL = "updateinterval";

  @JsonCreator
  public DataProviderConfiguration(final Map<String, Object> data) {super(data);}

  @JsonIgnore
  public Optional<Long> getUpdateInterval() { return get(UPDATE_INTERVAL, Long.class); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<DataProviderConfiguration, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final DataProviderConfiguration prior)
    {
      super(prior);
    }

    public P updateInterval(final Long updateInterval)
    {
      data(UPDATE_INTERVAL, updateInterval);
      return self();
    }

    public DataProviderConfiguration build() { return new DataProviderConfiguration(data);}
  }

  public static Builder<?> builder() { return new Builder(); }

  public static Builder<?> builder(final DataProviderConfiguration prior) { return new Builder(prior); }
}
