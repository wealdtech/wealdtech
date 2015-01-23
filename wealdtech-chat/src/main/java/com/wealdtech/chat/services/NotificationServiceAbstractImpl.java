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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic structure for handling notifications without the notification system itself
 */
public abstract class NotificationServiceAbstractImpl implements NotificationService
{
  private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceAbstractImpl.class);

  private final SubscriptionService subscriptionService;

  @Inject
  public NotificationServiceAbstractImpl(final SubscriptionService subscriptionService)
  {
    this.subscriptionService = subscriptionService;
  }

  @Override
  public void notify(final Message message)
  {
    final ImmutableList<Subscription> subscriptions;
    switch (message.getScope())
    {
      case INDIVIDUAL:
      case GROUP:
        // Obtain subscriptions for this topic and these users
        subscriptions = subscriptionService.obtainForTopicAndUsers(message.getTopic(), message.getTo());
        break;
      case EVERYONE:
        subscriptions = subscriptionService.obtainForTopic(message.getTopic());
        break;
      default:
        LOG.warn("Unknown message scope {}", message.getScope());
        subscriptions = ImmutableList.of();
    }
    for (final Subscription subscription: subscriptions)
    {
      if (!Objects.equal(subscription.getUser(), message.getFrom()))
      {
        sendPush(subscription.getUser());
      }
    }
  }

  abstract void sendPush(final String user);
}
