/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.positions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wealdtech.RangedWObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Position contains the details of an position in the contact's life. This is the abstract superclass; details of specific handles are
 * available in the subclasses.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(com.wealdtech.contacts.positions.WorkPosition.class)})
public abstract class Position<T extends Position<T>> extends RangedWObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(Position.class);

  // The type and realm jointly define the scope of the position
  protected static final String TYPE = "type";
  protected static final String KEY = "_key";

  @JsonCreator
  public Position(final Map<String, Object> data){ super(data); }

  /**
   * @return the type of the position.  This allows for correct serialization/deserialization
   */
  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  /**
   * @return the key for the position.  The key uniquely identifies the position for the given type
   */
  @JsonIgnore
  public String getKey(){ return get(KEY, String.class).get(); }

  @Override
  protected void validate()
  {
    checkState(exists(TYPE), "Position failed validation: must contain type");
    checkState(exists(KEY), "Position failed validation: must contain key");
  }

  public static class Builder<T extends Position<T>, P extends Builder<T, P>> extends RangedWObject.Builder<T, P>
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

    public P key(final String key)
    {
      data(KEY, key);
      return self();
    }
  }
}
