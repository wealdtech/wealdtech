/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * A day/time mask, providing specific days, days of weeks and times
 */
public class DTMask extends WObject<DTMask>
{
  private static final String INCLUDE = "include";

  private static final String DAYS_OF_WEEK = "daysofweek";
  private static final String TIMES_OF_DAY = "timesofday";

  @JsonCreator
  public DTMask(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public boolean isInclude() { return get(INCLUDE, Boolean.class).get(); }

  private static final TypeReference<ImmutableSet<Integer>> DAYS_OF_WEEK_TYPE_REF = new TypeReference<ImmutableSet<Integer>>(){};
  @JsonIgnore
  public ImmutableSet<Integer> getDaysOfWeek() { return get(DAYS_OF_WEEK, DAYS_OF_WEEK_TYPE_REF).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(INCLUDE)) { throw new DataError.Missing("DT mask needs 'include' information"); }
    if (!exists(DAYS_OF_WEEK)) { throw new DataError.Missing("DT mask needs 'daysofweek' information"); }
    if (!exists(TIMES_OF_DAY)) { throw new DataError.Missing("DT mask needs 'timesofday' information"); }
  }

}
