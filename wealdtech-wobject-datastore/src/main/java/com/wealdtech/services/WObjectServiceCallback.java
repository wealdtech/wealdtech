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
 */
public interface WObjectServiceCallback<T>
{
  public String getConditions();

  public void setConditionValues(T stmt);

  public WObjectServiceCallback<T> setString(T stmt, int index, @Nullable String val);

  public WObjectServiceCallback<T> setStringArray(T stmt, int index, @Nullable ImmutableCollection<String> val);

  public WObjectServiceCallback<T> setWID(T stmt, int index, @Nullable WID<?> val);

  public WObjectServiceCallback<T> setWIDArray(T stmt, int index, @Nullable ImmutableCollection<WID<?>> val);

  public WObjectServiceCallback<T> setLong(T stmt, int index, @Nullable Long val);
}
