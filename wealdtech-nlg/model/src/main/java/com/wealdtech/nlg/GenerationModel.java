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
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.collect.IntervalMultimap;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A model of text for generation
 */
public class GenerationModel extends WObject<GenerationModel> implements Comparable<GenerationModel>
{
  private static final String NAME = "name";
  private static final String SELECTIONS = "selections";

  @JsonCreator
  public GenerationModel(final Map<String, Object> data){super(data);}

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(ID), "Generation model failed validation: missing ID");
    checkState(exists(NAME), "Generation model failed validation: missing name");
    checkState(exists(SELECTIONS), "Generation model failed validation: missing selections");
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<GenerationModel> getId(){ return super.getId(); }

  @JsonIgnore
  public String getName(){ return get(NAME, String.class).get(); }

  private static final TypeReference<IntervalMultimap<Integer, String>> SELECTIONS_TYPE_REF = new TypeReference<IntervalMultimap<Integer, String>>(){};
  @JsonIgnore
  public IntervalMultimap<Integer, String> getSelections(){ return get(SELECTIONS, SELECTIONS_TYPE_REF).get(); }


  public static class Builder<P extends Builder<P>> extends WObject.Builder<GenerationModel, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GenerationModel prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P selections(final IntervalMultimap<Integer, String> selections)
    {
      data(SELECTIONS, selections);
      return self();
    }

    public GenerationModel build()
    {
      return new GenerationModel(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GenerationModel prior)
  {
    return new Builder(prior);
  }
}