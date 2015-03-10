/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto;

import com.google.common.base.Optional;

/**
 * Interface for Robert data providers.
 * A data provider provides a specific piece of data.  The availability of the data might change over time, as might the data
 * itself.  The data provider provides notifications when the provided data changes as well as when the state of the provider
 * changes.
 */
public interface DataProvider<T>
{
  /**
   * @return the current value of the data
   */
  Optional<T> get();

  /**
   * @return the current state of the data provider
   */
  DataProviderState getState();

  /**
   * Add a listener to be informed when the data provider changes state
   * @param listener the listener to be added
   */
  void addOnDataProviderStateChangedListener(OnDataProviderStateChangedListener listener);

  /**
   * Remove a listener to be informed when the data provider changes state
   * @param listener the listener to be removed
   */
  void removeOnDataProviderStateChangedListener(OnDataProviderStateChangedListener listener);

  /**
   * Add a listener to be informed when the data provided changes
   * @param listener the listener to be added
   */
  void addOnDataProviderDataChangedListener(OnDataProviderDataChangedListener<T> listener);

  /**
   * Remove a listener to be informed when the data provided changes
   * @param listener the listener to be Remove
   */
  void removeOnDataProviderDataChangedListener(OnDataProviderDataChangedListener<T> listener);

  /**
   * Start providing data
   * @throws IllegalStateException if the provider is not configured
   */
  void startProviding() throws IllegalStateException;

  /**
   * Stop providing data
   */
  void stopProviding();

  /**
   * Obtain the name of this data provider
   */
  String getName();
}
