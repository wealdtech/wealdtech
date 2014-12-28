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
import com.google.common.collect.ImmutableMultimap;
import com.wealdtech.chat.Chat;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Resource for chat methods
 */
@Path("/")
public class ChatResource
{
  /**
   * Obtain chats for multiple topics
   */
  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ImmutableMultimap<String, Chat> getChats(@QueryParam("since") final DateTime since)
  {
    return ImmutableMultimap.of();
  }

  /**
   * Obtain chats for a single topic
   * @param topic
   */
  @Timed
  @GET
  @Path("{topic: .*+}")
  @Produces(MediaType.APPLICATION_JSON)
  public ImmutableList<Chat> getChats(@PathParam("topic") final String topic,
                                      @QueryParam("since") final DateTime since)
  {
    return ImmutableList.of();
  }

  /**
   * Add a chat to a topic
   * @param topic
   * @param chat
   */
  @Timed
  @POST
  @Path("{topic: .*+}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void addChat(@PathParam("topic") final String topic, final Chat chat)
  {

  }
}
