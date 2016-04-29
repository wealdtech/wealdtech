/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.rekt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wealdtech.WObject;

import java.util.Map;

public class Value extends WObject<Value> implements Comparable<Value>
{
  @JsonCreator
  public Value(final Map<String, Object> data)
  {
    super(data);
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Value, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Value prior)
    {
      super(prior);
    }

    public Value build()
    {
      return new Value(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Value prior)
  {
    return new Builder(prior);
  }
}
