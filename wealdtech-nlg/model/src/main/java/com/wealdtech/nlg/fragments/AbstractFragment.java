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
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import com.wealdtech.nlg.GenerationModel;
import com.wealdtech.nlg.GenerationParameters;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public abstract class AbstractFragment implements Fragment
{
  private final Random random;

  private final GenerationModel model;

  public AbstractFragment(final GenerationModel model)
  {
    this.model = model;
    this.random = new Random();
  }

  /**
   * Pick a suitable phrase given the generation parameters
   * @param params the parameters passed to aid generation of the result
   * @return the result
   */
  protected String pick(final GenerationParameters params)
  {
    final Collection<String> results = model.getSelections().get(Range.closedOpen(params.getInformality(), params.getInformality() + 1));
    if (results == null || results.isEmpty())
    {
      throw new DataError.Bad("No item covering " + params.getInformality());
    }
    final int selectedEntry = random.nextInt(results.size());
    final Iterator<String> iterator = results.iterator();
    for (int i = 0; i < selectedEntry; i++)
    {
      iterator.next();
    }
    return iterator.next();
  }

  /**
   * A simple search/replace for the arguments
   * @param source the source text
   * @param args the arguments.  Note that although this is a multimap the implementation ssumes only one value per key
   * @return the replacement text
   */
  protected String replace(final String source, final ImmutableMultimap<String, String> args)
  {
    String result = source;
    for (final Map.Entry<String, String> entry : args.entries())
    {
      result = result.replace("{" + entry.getKey() + "}", entry.getValue());
    }
    return result;
  }
}
