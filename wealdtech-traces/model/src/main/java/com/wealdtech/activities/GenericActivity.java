/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.activities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

/**
 * A generic activity.
 */
@JsonTypeName("generic")
public class GenericActivity extends Activity<GenericActivity> implements Comparable<GenericActivity>
{
  @JsonCreator
  public GenericActivity(final Map<String, Object> data){ super(data); }

  public static class Builder<P extends Builder<P>> extends Activity.Builder<GenericActivity, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GenericActivity prior)
    {
      super(prior);
    }

    public GenericActivity build()
    {
      return new GenericActivity(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GenericActivity prior)
  {
    return new Builder(prior);
  }
}
