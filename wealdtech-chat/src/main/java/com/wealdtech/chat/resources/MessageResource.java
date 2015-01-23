/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.services.MessageService;
import com.wealdtech.chat.services.PushNotificationService;
import com.wordnik.swagger.annotations.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Resource for chat methods
 */
@Path("/messages")
@Api(value = "/messages", description = "Obtain and update chats")
public class MessageResource
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);

  private final MessageService messageService;
  private final PushNotificationService notificationService;

  @Inject
  public MessageResource(final MessageService messageService,
                         final PushNotificationService notificationService)
  {
    this.messageService = messageService;
    this.notificationService = notificationService;
  }

  @Timed
  @GET
  @Produces({MediaType.APPLICATION_JSON, ChatMediaType.V1_JSON})
  @ApiOperation(value = "Obtain all chats", notes = "Obtain all chats regardless of topic")
  @ApiResponses(value={@ApiResponse(code=404, message="Chat not found")})
  public ImmutableMultimap<String, Message> getChats(@ApiParam(value = "time that last set of chats were obtained", required = false) @QueryParam("since") final DateTime since)
  {
    final ImmutableList<Message> chats = messageService.getChats(null, null);
    if (chats.isEmpty())
    {
      return ImmutableMultimap.of();
    }
    else
    {
      return Multimaps.index(chats, new Function<Message, String>()
      {
        @Nullable
        @Override
        public String apply(final Message input)
        {
          return input.getTopic();
        }
      });
    }
  }

  /**
   * Obtain chats for a single topic
   * @param topic the name of the topic for which to fetch chats
   */
  @Timed
  @GET
  @Path("{topic: .*+}")
  @Produces(MediaType.APPLICATION_JSON)
  public ImmutableList<Message> getChats(@PathParam("topic") final String topic,
                                      @QueryParam("since") final DateTime since)
  {
    return messageService.getChats(null, topic);
  }

  /**
   * Add a message to a topic
   * @param message the message
   */
  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void addChat(final Message message)
  {
    messageService.add(message);
    notificationService.notify(message);
  }
}
