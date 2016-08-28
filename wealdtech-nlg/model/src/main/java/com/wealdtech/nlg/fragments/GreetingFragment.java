/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlg.fragments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Range;
import com.wealdtech.nlg.GenerationParameters;
import com.wealdtech.nlg.Selection;

import java.util.Map;

/**
 * A fragment that generates a greeting.
 */
@JsonTypeName("greeting")
public class GreetingFragment extends Fragment<GreetingFragment> implements Comparable<GreetingFragment>
{
  private static final String _TYPE = "greeting";
  private static final Selection SELECTION = Selection.builder()
                                           .option(Range.closedOpen(0, 50), "Dear {first name}")
                                           .option(Range.closedOpen(40, 70), "Hi {first name}")
                                           .option(Range.closedOpen(60, 90), "Hi")
                                           .option(Range.closedOpen(90, 100), "Hey")
                                           .build();

  @JsonCreator
  public GreetingFragment(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    data = super.preCreate(data);

    // Set our defining type and our selection
    data.put(TYPE, _TYPE);
    data.put(SELECTIONS, ImmutableList.of(SELECTION));

    return data;
  }

  @Override
  public String generate(final GenerationParameters params,
                         final ImmutableMultimap<String, String> args)
  {
    final String snippet = pick(params);
    return replace(snippet, args);
  }

  public static class Builder<P extends Builder<P>> extends Fragment.Builder<GreetingFragment, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GreetingFragment prior)
    {
      super(prior);
    }

    public GreetingFragment build()
    {
      return new GreetingFragment(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GreetingFragment prior)
  {
    return new Builder(prior);
  }
}
