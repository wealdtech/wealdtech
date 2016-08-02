/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.collect.ImmutableCollection;
import com.wealdtech.WID;

import javax.annotation.Nullable;

/**
 * Callbacks for the WObject service.  This allows calls to the WObject service to obtain parameters
 */
public interface WObjectServiceCallback<T>
{
  /**
   * Obtain the query for the callback.  This is only used when running a generic query.  In all other cases it is ignored
   */
  @Nullable String getQuery();

  /**
   * Obtain the conditions when obtaining or updating objects.
   */
  @Nullable String getConditions();

  /**
   * Obtain the values for conditions when obtaining or updating objects.
   */
  void setConditionValues(T stmt);

  /**
   * Obtain the required ordering when obtaining objects
   */
  @Nullable String getOrder();

  /**
   * Set a string value
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setString(T stmt, int index, @Nullable String val);

  /**
   * Set an array of string values
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setStringArray(T stmt, int index, @Nullable ImmutableCollection<String> val);

  /**
   * Set an array of context-insensitive string values
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setCIStringArray(T stmt, int index, @Nullable ImmutableCollection<String> val);

  /**
   * Set a WID value
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setWID(T stmt, int index, @Nullable WID<?> val);

  /**
   * Set an array of WID values
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setWIDArray(T stmt, int index, @Nullable ImmutableCollection<? extends WID<?>> val);

  /**
   * Set a long value
   * @param stmt the statement
   * @param index the index of the value to set
   * @param val the value to set
   * @return the callback
   */
  WObjectServiceCallback<T> setLong(T stmt, int index, @Nullable Long val);
}
