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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import com.wealdtech.Application;
import com.wealdtech.chat.Subscription;
import com.wealdtech.chat.Topic;
import com.wealdtech.User;

/**
 * Interface defining subscription service methods
 */
public interface SubscriptionService
{
  /**
   * Add a subscription
   * @param subscription the subscription
   */
  void add(Subscription subscription);

  /**
   * Remove a subscription
   * @param subscriptionId the ID of the subscription to remove
   */
  void remove(WID<Subscription> subscriptionId);

  /**
   * Obtain all subscriptions to a particular topic
   * @param appId the ID of the application to which the topic belongs
   * @param topicId the ID of the topic to which to subscribe
   * @return all subscriptions to this topic
   */
  ImmutableSet<Subscription> obtain(final WID<Application> appId, WID<Topic> topicId);

  /**
   * Obtain all subscriptions to a particular topic and set of users
   * @param appId the ID of the application to which the topic belongs
   * @param topicId the ID of the topic to which to subscribe
   * @param userIds the IDs of the users for which to obtain the subscriptions
   * @return all subscriptions to this topic by the users
   */
  ImmutableSet<Subscription> obtain(final WID<Application> appId, WID<Topic> topicId, ImmutableCollection<WID<User>> userIds);
}
