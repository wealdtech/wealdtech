/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * An extended WObject that includes from and (optionally) to specifiers to define the range over which the object is valid
 */
public abstract class RangedWObject<T extends WObject<T>> extends WObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(WObject.class);

  private static final String VALID_FROM = "_validfrom";
  private static final String VALID_TO = "_validto";

  public RangedWObject(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public LocalDateTime getValidFrom(){ return get(VALID_FROM, LocalDateTime.class).get();}

  @JsonIgnore
  public Optional<LocalDateTime> getValidTo(){ return get(VALID_TO, LocalDateTime.class);}

  protected void validate()
  {
    super.validate();
    checkState(exists(VALID_FROM), "Item from is required");
  }

  public static class Builder<T extends RangedWObject<T>, P extends Builder<T, P>> extends WObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P validFrom(final LocalDateTime from)
    {
      data(VALID_FROM, from);
      return self();
    }

    public P validTo(final LocalDateTime to)
    {
      data(VALID_TO, to);
      return self();
    }
  }
}
