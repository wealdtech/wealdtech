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
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.Application;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.chat.services.ChatService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Resource for chat methods
 */
@Path("topics/{topicid: [0-9A-Za-z]+}/messages")
@Singleton
public class MessageResource
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);

  private final ChatService chatService;

  @Inject
  public MessageResource(final ChatService chatService)
  {
    this.chatService = chatService;
  }

  /**
   * Obtain a single message
   */
  @Timed
  @GET
  @Path("{messageid: [A-Za-z0-9]+}")
  @Produces({MediaType.APPLICATION_JSON, ChatMediaType.V1_JSON})
  public Message obtainMessage(@Context final Application app,
                               @Context final User user,
                            @PathParam("topicid") final WID<Topic> topicId,
                            @PathParam("messageid") final WID<Message> messageId)
  {
    return chatService.obtainMessage(app, user, topicId, messageId);
  }

  /**
   * Obtain all new messages in a single topic since a given time
   */
  @Timed
  @GET
  @Produces({MediaType.APPLICATION_JSON, ChatMediaType.V1_JSON})
  public ImmutableList<Message> obtainMessagesSince(@Context final Application app,
                                                    @Context final User user,
                                                    @PathParam("topicid") final WID<Topic> topicId,
                                                    @Nullable @QueryParam("since") final DateTime since)
  {
    return chatService.obtainMessagesSince(app, user, topicId, since);
  }

  /**
   * Add a message to a topic
   *
   * @param message the message
   */
  @Timed
  @POST
  @Consumes({MediaType.APPLICATION_JSON, ChatMediaType.V1_JSON})
  public void createMessage(@Context final Application app,
                            @Context final User user,
                            @PathParam("topicid") final WID<Topic> topicId, final Message message)
  {
    // Create the message
    chatService.createMessage(app, user, topicId, message);
  }
}
