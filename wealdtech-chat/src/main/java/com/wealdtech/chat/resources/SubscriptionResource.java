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
import com.google.inject.Inject;
import com.wealdtech.chat.Subscription;
import com.wealdtech.chat.services.SubscriptionService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Resource for subscriptions
 */
@Path("/subscriptions")
@Api(value = "/subscriptions", description = "Manage subscriptions to topics")
public class SubscriptionResource
{
  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionResource.class);

  private final SubscriptionService service;

  @Inject
  public SubscriptionResource(final SubscriptionService service)
  {
    this.service = service;
  }

  @Timed
  @POST
  @Path("{topic: .*+}")
  @ApiOperation(value = "Subscribe to a topic")
  public void subscribe(@ApiParam(value = "the ID of the topic to which to subscribe", required = true) @PathParam("topic") final String topic)
  {
    service.add(Subscription.builder().user("jgm").topic(topic).build());
  }

  @Timed
  @DELETE
  @Path("{topic: .*+}")
  @ApiOperation(value = "Unsubscribe from a topic")
  public void unsubscribe(@PathParam("topic") final String topic)
  {
    service.remove(Subscription.builder().user("jgm").topic(topic).build());
  }
}
