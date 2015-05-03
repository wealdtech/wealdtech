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
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.WID;
import com.wealdtech.Application;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Subscription;
import com.wealdtech.chat.Topic;
import com.wealdtech.notifications.Notification;
import com.wealdtech.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for Wealdtech notifications
 */
public class PushNotificationService
{
  private static final Logger LOG = LoggerFactory.getLogger(PushNotificationService.class);

  private final SubscriptionService subscriptionService;
  private final NotificationService notificationService;

  @Inject
  public PushNotificationService(final SubscriptionService subscriptionService,
                                 final NotificationService notificationService)
  {
    this.subscriptionService = subscriptionService;
    this.notificationService = notificationService;
  }

  public void notify(final WID<Application> appId, final WID<Topic> topicId, final Message message)
  {
    final ImmutableSet<Subscription> subscriptions;
    switch (message.getScope())
    {
      case INDIVIDUAL:
      case GROUP:
        // Obtain subscriptions for this topic and these users
        subscriptions = subscriptionService.obtain(appId, topicId, message.getTo());
        break;
      case EVERYONE:
        subscriptions = subscriptionService.obtain(appId, topicId);
        break;
      default:
        LOG.warn("Unknown message scope {}", message.getScope());
        subscriptions = ImmutableSet.of();
    }
    for (final Subscription subscription: subscriptions)
    {
      if (!Objects.equal(subscription.getUser(), message.getFrom()))
      {
        notificationService.notify(ImmutableSet.of(subscription.getUser()), Notification.builder().data("msg", "foo").build());
      }
    }
  }
}
