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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.wealdtech.WObject;

import javax.annotation.Nullable;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

public class ResultSet extends WObject<ResultSet> implements Comparable<ResultSet>
{
  private static final String STATE = "state";
  private static final String ELEMENTS = "elements";
  private static final TypeReference<ImmutableList<Element>> ELEMENTS_TYPE_REF = new TypeReference<ImmutableList<Element>>() {};

  @JsonCreator
  public ResultSet(final Map<String, Object> data){ super(data); }

  @JsonIgnore
  @Nullable
  public Element obtainElement(final String name)
  {
    for (final Element element : getElements())
    {
      if (Objects.equal(element.getName(), name))
      {
        return element;
      }
    }
    return null;
  }

  public static ResultSet fromDefinition(final ElementDefinitionGroup definition,
                                         final ImmutableMultimap<String, String> inputs,
                                         final AdditionalInfo additionalInfo)
  {
    final ImmutableList.Builder<Element> elementsB = ImmutableList.builder();

    for (final ElementDefinition<?> elementDefinition : definition.getElementDefinitions())
    {
      final Element element =
          Element.fromDefinition(elementDefinition, ImmutableList.copyOf(inputs.get(elementDefinition.getName())), additionalInfo);
      if (element != null)
      {
        elementsB.add(element);
      }
    }
    return ResultSet.builder().elements(elementsB.build()).state(State.INVALID).build();
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(STATE), "ResultSet failed validation: missing state");
    checkState(exists(ELEMENTS), "ResultSet failed validation: missing elements");
  }

  @JsonIgnore
  public ImmutableList<Element> getElements(){ return get(ELEMENTS, ELEMENTS_TYPE_REF).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<ResultSet, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final ResultSet prior)
    {
      super(prior);
    }

    public P state(final State state)
    {
      data(STATE, state);
      return self();
    }

    public P elements(final ImmutableList<Element> elements)
    {
      data(ELEMENTS, elements);
      return self();
    }

    public ResultSet build()
    {
      return new ResultSet(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final ResultSet prior)
  {
    return new Builder(prior);
  }
}
