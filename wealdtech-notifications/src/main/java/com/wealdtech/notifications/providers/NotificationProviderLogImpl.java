/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.notifications.providers;

import com.google.common.collect.ImmutableSet;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class NotificationProviderLogImpl implements NotificationProvider<WObject<?>>
{
  private static final Logger LOG = LoggerFactory.getLogger(NotificationProviderLogImpl.class);

  @Override
  public void notify(final String appId, final String accessKey, final ImmutableSet<String> recipients, final WObject<?> msg)
  {
    LOG.info("Message to {}: {}", recipients, msg);
  }
}
