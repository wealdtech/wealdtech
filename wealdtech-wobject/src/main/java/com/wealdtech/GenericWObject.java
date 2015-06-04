/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A generic WObject. This is a minimal implementation of the WObject, useful for situations where the data returned is undefined or
 * just not worth bothering creating its own object
 */
public class GenericWObject extends WObject<GenericWObject> implements Comparable<GenericWObject>
{
  private static final Logger LOG = LoggerFactory.getLogger(GenericWObject.class);

  @JsonCreator
  public GenericWObject(final Map<String, Object> data)
  {
    super(data);
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<GenericWObject, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GenericWObject prior)
    {
      super(prior);
    }

    public GenericWObject build()
    {
      return new GenericWObject(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GenericWObject prior)
  {
    return new Builder(prior);
  }
}
