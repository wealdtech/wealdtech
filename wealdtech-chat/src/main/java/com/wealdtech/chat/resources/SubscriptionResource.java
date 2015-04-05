/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.WID;
import com.wealdtech.chat.Subscription;
import com.wealdtech.chat.User;
import com.wealdtech.chat.services.SubscriptionService;
import com.wealdtech.services.WIDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;

/**
 * Resource for subscriptions
 */
@Path("/subscriptions")
public class SubscriptionResource
{
  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionResource.class);

  private final SubscriptionService service;
  private final WIDService widService;

  @Inject
  public SubscriptionResource(final SubscriptionService service, final WIDService widService)
  {
    this.service = service;
    this.widService = widService;
  }

  @Timed
  @POST
  @Path("{topic: .*+}")
  public void subscribe(@QueryParam("topic") final String topic)
  {
    final String user = "jgm";
    service.add(Subscription.builder().id(widService.<Subscription>obtain()).user(user).topic(topic).build());
  }

  @Timed
  @DELETE
  @Path("{topic: .*+}/{userid: [0-9A-Fa-f]+}")
  public void unsubscribe(@PathParam("topic") final String topic)
  {
    final User user = User.builder().id(WID.<User>generate()).name("Jim").build();
    final ImmutableList<Subscription> subscriptions = service.obtainForTopicAndUsers(topic, ImmutableSet.of(user.getId()));
    if (subscriptions.isEmpty())
    {
      LOG.warn("Attempt to unsubscribe from nonexistent subscription {}/{}", topic, user);
    }
    service.remove(subscriptions.iterator().next().getId());
  }
}
