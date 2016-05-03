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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Results of a parse of input by NLP
 */
public class ParseResults extends WObject<ParseResults> implements Comparable<ParseResults>
{
  private static final String INPUT = "input";
  private static final String TOKENS = "tokens";
  private static final String PHRASES = "phrases";

  public ParseResults(final Map<String, Object> data){super(data);}

  @Override
  protected void validate()
  {
    checkState(exists(INPUT), "Parse results failed validation: missing input");
    checkState(exists(TOKENS), "Parse results failed validation: missing tokens");
    checkState(exists(PHRASES), "Parse results failed validation: missing phrases");
  }

  @JsonIgnore
  public String getInput() { return get(INPUT, String.class).get(); }

  private static final TypeReference<ImmutableList<Token>> TOKENS_TYPE_REF = new TypeReference<ImmutableList<Token>>(){};
  @JsonIgnore
  public ImmutableList<Token> getTokens() { return get(TOKENS, TOKENS_TYPE_REF).get(); }

  private static final TypeReference<ImmutableList<Token>> PHRASES_TYPE_REF = new TypeReference<ImmutableList<Token>>(){};
  @JsonIgnore
  public ImmutableList<Token> getPhrases() { return get(PHRASES, PHRASES_TYPE_REF).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<ParseResults, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final ParseResults prior)
    {
      super(prior);
    }

    public P input(final String input)
    {
      data(INPUT, input);
      return self();
    }

    public P tokens(final ImmutableList<Token> tokens)
    {
      data(TOKENS, tokens);
      return self();
    }

    public P phrases(final ImmutableList<Token> phrases)
    {
      data(PHRASES, phrases);
      return self();
    }

    public ParseResults build()
    {
      return new ParseResults(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final ParseResults prior)
  {
    return new Builder(prior);
  }
}
