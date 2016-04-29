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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import javax.annotation.Nullable;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

public class Element extends WObject<Element> implements Comparable<Element>
{
  private static final String NAME = "name";
  private static final String INPUTS = "inputs";
  private static final String RESULTS = "results";

  @JsonCreator
  public Element(final Map<String, Object> data) { super(data); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(NAME), "Element failed validation: missing name");
    checkState(exists(INPUTS), "Element failed validation: missing inputs");
    checkState(exists(RESULTS), "Element failed validation: missing results");
  }

  @JsonIgnore
  public String getName() { return get(NAME, String.class).get(); }

  private static final TypeReference<ImmutableList<String>> INPUTS_TYPE_REF = new TypeReference<ImmutableList<String>>(){};
  @JsonIgnore
  public ImmutableList<String> getInputs() { return get(INPUTS, INPUTS_TYPE_REF).get(); }

  private static final TypeReference<ImmutableList<Result>> RESULTS_TYPE_REF = new TypeReference<ImmutableList<Result>>(){};
  @JsonIgnore
  public ImmutableList<Result> getResults() { return get(RESULTS, RESULTS_TYPE_REF).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Element, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Element prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P inputs(final ImmutableList<String> inputs)
    {
      data(INPUTS, inputs);
      return self();
    }

    public P results(final ImmutableList<Result> results)
    {
      data(RESULTS, results);
      return self();
    }

    public Element build()
    {
      return new Element(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Element prior)
  {
    return new Builder(prior);
  }

  @Nullable
  public static <T> Element fromDefinition(final ElementDefinition<T> definition, final ImmutableList<String> inputs)
  {
    final Element.Builder<?> elementB = Element.builder().name(definition.getName()).inputs(inputs);
    if (inputs == null)
    {
      if (definition.isMandatory())
      {
        throw new DataError.Bad("Missing mandatory value " + definition.getName());
      }
    }
    else
    {
      elementB.results(definition.getGenerator().generate(inputs, definition.getValidator()));
    }
    return elementB.build();
  }
}
