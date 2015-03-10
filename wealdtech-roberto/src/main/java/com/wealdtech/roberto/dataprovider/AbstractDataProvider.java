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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.wealdtech.roberto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The abstract data provider
 */
public abstract class AbstractDataProvider<T> implements DataProvider<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(AbstractDataProvider.class);

  private List<OnDataProviderStateChangedListener> dataProviderStateChangedListeners;
  private List<OnDataProviderDataChangedListener<T>> dataProviderDataChangedListeners;

  private Optional<T> data;

  private String name;
  private long updateInterval;

  private Thread pollingThread;

  private DataProviderState providerState;
  private DataProviderConfigurationState configurationState;

  // Items handling obtaning data
  private boolean dataObtained = false;
  private final Object mutex = new Object();

  public AbstractDataProvider(final String name)
  {
    this.name = name;
    this.configurationState = DataProviderConfigurationState.NOT_CONFIGURED;
    this.providerState = DataProviderState.NOT_PROVIDING;
    this.updateInterval = 0;
    pollingThread = null;
    data = Optional.absent();
    this.dataProviderStateChangedListeners = Lists.newArrayList();
    this.dataProviderDataChangedListeners = Lists.newArrayList();
  }

  @Override
  public void addOnDataProviderStateChangedListener(final OnDataProviderStateChangedListener listener)
  {
    dataProviderStateChangedListeners.add(listener);
  }

  @Override
  public void removeOnDataProviderStateChangedListener(final OnDataProviderStateChangedListener listener)
  {
    dataProviderStateChangedListeners.remove(listener);
  }

  @Override
  public void addOnDataProviderDataChangedListener(final OnDataProviderDataChangedListener<T> listener)
  {
    dataProviderDataChangedListeners.add(listener);
  }

  @Override
  public void removeOnDataProviderDataChangedListener(final OnDataProviderDataChangedListener<T> listener)
  {
    dataProviderDataChangedListeners.remove(listener);
  }

  @Override
  public String getName(){ return name; }

  @Override
  public DataProviderState getState()
  {
    return providerState;
  }

  protected void configure(final DataProviderConfiguration configuration)
  {
    final Optional<Long> updateInterval = configuration.getUpdateInterval();
    if (updateInterval.isPresent())
    {
      this.updateInterval = updateInterval.get();
    }
  }

  /**
   * Set the state of this data provider
   *
   * @param state the new state of the data provider
   */
  protected void setState(final DataProviderState state)
  {
    if (this.providerState != state)
    {
      this.providerState = state;
      for (final OnDataProviderStateChangedListener listener : dataProviderStateChangedListeners)
      {
        listener.onDataProviderStateChanged(state);
      }
    }
  }

  protected void setConfigurationState(final DataProviderConfigurationState state)
  {
    this.configurationState = state;
  }

  public void startProviding() throws IllegalStateException
  {
    if (configurationState == DataProviderConfigurationState.NOT_CONFIGURED)
    {
      // We want to provide but aren't configured; note it but nothing else
      providerState = DataProviderState.AWAITING_CONFIGURATION;
    }
    else
    {
      if (isPollingProvider())
      {
        startPolling();
      }
      else
      {
        fetch();
      }
      providerState = DataProviderState.PROVIDING;
    }
  }

  public void stopProviding()
  {
    if (providerState == DataProviderState.PROVIDING ||
        providerState == DataProviderState.DEGRADED ||
        providerState == DataProviderState.FAILED)
    {
      pollingThread.interrupt();
      providerState = DataProviderState.NOT_PROVIDING;
    }
  }

  private void startPolling()
  {
    final Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        Thread.currentThread().setName(name + " provider");

        while (!Thread.currentThread().isInterrupted())
        {
          if (configurationState == DataProviderConfigurationState.CONFIGURED)
          {
            dataObtained = false;
            fetch();
          }
          try
          {
            Thread.sleep(updateInterval);
          }
          catch (final InterruptedException ignored) {}
        }
      }
    };

    pollingThread = new Thread(runnable);
    pollingThread.start();
  }

  private void stopPolling()
  {
    if (pollingThread != null)
    {
      final Thread pollingThread = this.pollingThread;
      this.pollingThread = null;
      pollingThread.interrupt();
    }
  }

  /**
   * Get the current data held by the provider.
   * Although this is immediate there is no guarantee about the freshness of the data.  In general the data should be obtained by
   * listening to changes with the {@link com.wealdtech.roberto.OnDataProviderDataChangedListener} interface
   * @return the current data
   * @throws IllegalStateException if the provider is not ready or able to provide data
   */
  @Override
  public Optional<T> get() throws IllegalStateException
  {
    if (configurationState == DataProviderConfigurationState.NOT_CONFIGURED)
    {
      throw new IllegalStateException(getName() + " provider not configured to provide data");
    }
    if (providerState == DataProviderState.NOT_PROVIDING || providerState == DataProviderState.AWAITING_CONFIGURATION)
    {
      throw new IllegalStateException(getName() + " provider not providing data");
    }

    return data;
  }

  /**
   * Fetch data immediately
   */
  public void fetch()
  {
    if (configurationState == DataProviderConfigurationState.NOT_CONFIGURED)
    {
      throw new IllegalStateException(getName() + " provider not configured to provide data");
    }
    if (providerState == DataProviderState.NOT_PROVIDING || providerState == DataProviderState.AWAITING_CONFIGURATION)
    {
      throw new IllegalStateException(getName() + " provider not providing data");
    }

    if (!dataObtained)
    {
      synchronized(mutex)
      {
        if (!dataObtained)
        {
          final T newData = obtainData();
          dataObtained = true;
          setData(newData);
        }
      }
    }
  }

  protected abstract T obtainData();

  /**
   * Set the data to a new value, notifying listeners if it is different from existing data
   *
   * @param data the new data; can be {@code null}
   */
  private void setData(final @Nullable T data)
  {
    if (!Objects.equal(data, this.data))
    {
      this.data = Optional.fromNullable(data);
      for (final OnDataProviderDataChangedListener<T> listener : dataProviderDataChangedListeners)
      {
        listener.onDataProviderDataChanged(data);
      }
    }
  }

  public boolean isPollingProvider()
  {
    return updateInterval != 0;
  }
}
