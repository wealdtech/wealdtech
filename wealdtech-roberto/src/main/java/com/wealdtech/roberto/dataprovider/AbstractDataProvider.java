/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto.dataprovider;

import com.google.common.collect.Lists;
import com.wealdtech.roberto.DataProvider;
import com.wealdtech.roberto.DataProviderState;
import com.wealdtech.roberto.OnDataProviderStateChangedListener;

import java.util.List;

/**
 * The abstract data provider
 */
public abstract class AbstractDataProvider<T> implements DataProvider<T>
{
  private List<OnDataProviderStateChangedListener> dataProviderStateChangedListeners;

  private DataProviderState state;

  public AbstractDataProvider()
  {
    state = DataProviderState.UNCONFIGURED;
    dataProviderStateChangedListeners = Lists.newArrayList();
  }

  /**
   * Add a listener for changes to the state of this data provider
   */
  @Override
  public void addOnDataProviderStateChangedListener(final OnDataProviderStateChangedListener listener)
  {
    dataProviderStateChangedListeners.add(listener);
  }

  @Override
  public DataProviderState getState()
  {
    return state;
  }

  /**
   * Set the state of this data provider
   * @param state the new state of the data provider
   */
  protected void setState(final DataProviderState state)
  {
    if (this.state != state)
    {
      this.state = state;
      for (final OnDataProviderStateChangedListener listener : dataProviderStateChangedListeners)
      {
        listener.onDataProviderStateChanged(state);
      }
    }
  }
}
