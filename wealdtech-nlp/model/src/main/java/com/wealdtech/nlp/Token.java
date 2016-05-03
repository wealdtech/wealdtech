/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 *
 */
public class Token extends WObject<Token> implements Comparable<Token>
{
  private static final String POSITION = "position";
  private static final String LENGTH = "length";
  private static final String INPUT = "input";
  private static final String ENTITY = "entity";

  public Token(final Map<String, Object> data){super(data);}

  @Override
  protected void validate()
  {
    checkState(exists(POSITION), "Token failed validation: missing position");
    checkState(exists(LENGTH), "Token failed validation: missing length");
    checkState(exists(INPUT), "Token failed validation: missing input");
    checkState(exists(ENTITY), "Token failed validation: missing entity");
  }

  @JsonIgnore
  public String getInput() { return get(INPUT, String.class).get(); }

  @JsonIgnore
  public long getPosition() { return get(POSITION, Long.class).get(); }

  @JsonIgnore
  public long getLength() { return get(LENGTH, Long.class).get(); }
  
  @JsonIgnore
  public String getEntity() { return get(ENTITY, String.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Token, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Token prior)
    {
      super(prior);
    }

    public P position(final Long position)
    {
      data(POSITION, position);
      return self();
    }

    public P length(final Long length)
    {
      data(LENGTH, length);
      return self();
    }

    public P input(final String input)
    {
      data(INPUT, input);
      return self();
    }

    public P entity(final String entity)
    {
      data(ENTITY, entity);
      return self();
    }

    public Token build()
    {
      return new Token(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Token prior)
  {
    return new Builder(prior);
  }
}
