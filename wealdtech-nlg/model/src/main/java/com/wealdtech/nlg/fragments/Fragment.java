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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Range;
import com.wealdtech.DataError;
import com.wealdtech.WObject;
import com.wealdtech.nlg.GenerationParameters;
import com.wealdtech.nlg.Selection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.wealdtech.Preconditions.checkState;

/**
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(GreetingFragment.class)})
public abstract class Fragment<T extends Fragment<T>> extends WObject<T> implements Comparable<T>
{
  protected static final String TYPE = "type";
  protected static final String SELECTIONS = "selections";

  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  private static final TypeReference<ImmutableList<Selection>> SELECTIONS_TYPE_REF = new TypeReference<ImmutableList<Selection>>(){};
  @JsonIgnore
  public ImmutableList<Selection> getSelections() { return get(SELECTIONS, SELECTIONS_TYPE_REF).get(); }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Fragment failed validation: must contain type");
  }

  @JsonCreator
  public Fragment(final Map<String, Object> data)
  {
    super(data);
    this.random = new Random();
  }

  public static class Builder<T extends Fragment<T>, P extends Builder<T, P>> extends WObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P type(final String type)
    {
      data(TYPE, type);
      return self();
    }

    public P selections(final ImmutableList<Selection> selections)
    {
      data(SELECTIONS, selections);
      return self();
    }
  }

  @JsonIgnore private final Random random;

  /**
   * Pick a suitable phrase given the generation parameters
   *
   * @param params the parameters passed to aid generation of the result
   *
   * @return the result
   */
  protected String pick(final GenerationParameters params)
  {
    final Collection<String> results = getSelections().iterator().next().getOptions().get(Range.closed(params.getInformality(), params.getInformality()));
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
   *
   * @param source the source text
   * @param args the arguments.  Note that although this is a multimap the implementation ssumes only one value per key
   *
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

  public abstract String generate(final GenerationParameters params, final ImmutableMultimap<String, String> args);
}
