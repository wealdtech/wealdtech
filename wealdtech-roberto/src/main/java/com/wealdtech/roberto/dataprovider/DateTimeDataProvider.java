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

import com.wealdtech.roberto.DataProviderConfiguration;
import com.wealdtech.roberto.DataProviderConfigurationState;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data provider which returns the current date
 */
public class DateTimeDataProvider extends AbstractDataProvider<DateTime>
{
  private static final Logger LOG = LoggerFactory.getLogger(DateTimeDataProvider.class);

  public DateTimeDataProvider()
  {
    super("Date/time");
    // Update every second
    configure(DataProviderConfiguration.builder().updateInterval(1000L).build());
  }

  @Override
  protected void configure(final DataProviderConfiguration configuration)
  {
    super.configure(configuration);
    setConfigurationState(DataProviderConfigurationState.CONFIGURED);
  }

  @Override
  protected DateTime obtainData()
  {
    return new DateTime();
  }
}
