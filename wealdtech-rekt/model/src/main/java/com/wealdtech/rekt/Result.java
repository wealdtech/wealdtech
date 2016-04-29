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
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

public class Result extends WObject<Result> implements Comparable<Result>
{
  private static final String VALUE = "value";
  private static final String POTENTIAL_VALUES = "potentialvalues";
  private static final String STATE = "state";

  @JsonCreator
  public Result(final Map<String, Object> data) { super(data); }

  @JsonIgnore
  public <T> Optional<T> getValue(final Class<T> klazz) { return get(VALUE, klazz); }

  @JsonIgnore
  public <T> Optional<T> getValue(final TypeReference<T> typeref) { return get(VALUE, typeref); }

  @JsonIgnore
  public <T> Optional<ImmutableList<T>> getPotentialValues(final TypeReference<ImmutableList<T>> typeref) { return get(POTENTIAL_VALUES, typeref);}
//  private static final TypeReference<ImmutableList<Value>> POTENTIAL_VALUES_TYPE_REF = new TypeReference<ImmutableList<Value>>(){};
//  @JsonIgnore
//  public Optional<ImmutableList<Value>> getValues() { return get(POTENTIAL_VALUES, POTENTIAL_VALUES_TYPE_REF); }

  @JsonIgnore
  public State getState() { return get(STATE, State.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(STATE), "Value failed validation: missing state");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Result, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Result prior)
    {
      super(prior);
    }

    public P value(final Object value)
    {
      data(VALUE, value);
      return self();
    }

    public P potentialValues(final ImmutableList<? extends Object> potentialValues)
    {
      data(POTENTIAL_VALUES, potentialValues);
      return self();
    }

    public P state(final State state)
    {
      data(STATE, state);
      return self();
    }

    public Result build()
    {
      return new Result(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Result prior)
  {
    return new Builder(prior);
  }
}
