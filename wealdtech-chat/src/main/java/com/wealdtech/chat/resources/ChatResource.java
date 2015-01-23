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
import com.wealdtech.chat.Chat;
import com.wealdtech.chat.services.ChatService;
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
@Path("/chats")
@Api(value = "/chats", description = "Obtain and update chats")
public class ChatResource
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatResource.class);

  private final ChatService service;

  @Inject
  public ChatResource(final ChatService service)
  {
    this.service = service;
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Obtain all chats", notes = "Obtain all chats regardless of topic")
  @ApiResponses(value={@ApiResponse(code=404, message="Chat not found")})
  public ImmutableMultimap<String, Chat> getChats(@ApiParam(value = "time that last set of chats were obtained", required = false) @QueryParam("since") final DateTime since)
  {
    final ImmutableList<Chat> chats = service.getChats(null, null);
    if (chats.isEmpty())
    {
      return ImmutableMultimap.of();
    }
    else
    {
      return Multimaps.index(chats, new Function<Chat, String>()
      {
        @Nullable
        @Override
        public String apply(final Chat input)
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
  public ImmutableList<Chat> getChats(@PathParam("topic") final String topic,
                                      @QueryParam("since") final DateTime since)
  {
    return service.getChats(null, topic);
  }

  /**
   * Add a chat to a topic
   * @param chat the chat
   */
  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void addChat(final Chat chat)
  {
    service.add(chat);
  }
}
