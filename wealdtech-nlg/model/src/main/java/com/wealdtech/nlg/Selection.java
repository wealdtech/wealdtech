/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Range;
import com.wealdtech.WObject;
import com.wealdtech.collect.IntervalMultimap;

import java.util.Map;

/**
 * A set of items that are selected based on familiarity and formality criteria
 */
public class Selection extends WObject<Selection> implements Comparable<Selection>
{
  private static final String OPTIONS = "options";

  @JsonCreator
  public Selection(final Map<String, Object> data){super(data);}

  private static final TypeReference<IntervalMultimap<Integer, String>> OPTIONS_TYPE_REF =
      new TypeReference<IntervalMultimap<Integer, String>>() {};

  @JsonIgnore
  public IntervalMultimap<Integer, String> getOptions()
  {
    return get(OPTIONS, OPTIONS_TYPE_REF).or(new IntervalMultimap<Integer, String>());
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Selection, P>
  {
    private final IntervalMultimap<Integer, String> options = new IntervalMultimap<>();

    public Builder()
    {
      super();
    }

    public Builder(final Selection prior)
    {
      super(prior);
    }

    public P option(final Range<Integer> range, final String snippet)
    {
      options.put(range, snippet);
      return self();
    }

    public Selection build()
    {
      data.put(OPTIONS, options);
      return new Selection(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Selection prior)
  {
    return new Builder(prior);
  }
}
