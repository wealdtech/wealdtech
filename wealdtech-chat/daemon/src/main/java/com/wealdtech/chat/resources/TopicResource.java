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
import com.wealdtech.Application;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.chat.Topic;
import com.wealdtech.chat.services.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

/**
 * Resource for topic methods
 */
@Path("topics")
public class TopicResource
{
  private static final Logger LOG = LoggerFactory.getLogger(TopicResource.class);

  private final ChatService chatService;

  @Inject
  public TopicResource(final ChatService chatService)
  {
    this.chatService = chatService;
  }

  /**
   * Remove a topic
   */
  @Timed
  @DELETE
  @Path("{topicid: [A-Za-z0-9]+}")
  public void removeTopic(@Context final Application app,
                          @Context final User user,
                          @PathParam("topicid") final WID<Topic> topicId)
  {
    chatService.removeTopic(app, user, chatService.obtainTopic(app, user, topicId));
  }
}
