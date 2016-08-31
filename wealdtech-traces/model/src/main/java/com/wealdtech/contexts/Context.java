/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contexts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Context provides information about a given
 * @param <T> the type of the particular context
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(GenericContext.class),
               @JsonSubTypes.Type(LocationContext.class),
               @JsonSubTypes.Type(NamedEntityContext.class)})
public abstract class Context<T extends Context<T>> extends WObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(Context.class);

  // The type and realm jointly define the scope of the Context
  protected static final String TYPE = "type";

  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Context failed validation: must contain type");
  }

  @JsonCreator
  public Context(final Map<String, Object> data){ super(data); }

  public static class Builder<T extends Context<T>, P extends Builder<T, P>> extends WObject.Builder<T, P>
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
  }
}