/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dummy notification implementation which outputs notifications using the console
 */
public class NotificationServiceLogImpl extends NotificationServiceAbstractImpl
{
  private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceAbstractImpl.class);

  @Inject
  public NotificationServiceLogImpl(final SubscriptionService subscriptionService)
  {
    super(subscriptionService);
  }

  @Override
  void sendPush(final String user)
  {
    LOG.error("Pushing to {}", user);
  }
}
