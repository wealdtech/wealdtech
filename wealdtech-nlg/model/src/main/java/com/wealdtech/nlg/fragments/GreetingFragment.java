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

import com.google.common.collect.ImmutableMultimap;
import com.wealdtech.nlg.GenerationModel;
import com.wealdtech.nlg.GenerationParameters;

/**
 * A fragment that generates a greeting
 */
public class GreetingFragment extends AbstractFragment
{
  public GreetingFragment(final GenerationModel model)
  {
    super(model);
  }

  @Override
  public String generate(final GenerationParameters params, final ImmutableMultimap<String, String> args)
  {
    String selection = pick(params);
    return replace(selection, args);
  }
}
