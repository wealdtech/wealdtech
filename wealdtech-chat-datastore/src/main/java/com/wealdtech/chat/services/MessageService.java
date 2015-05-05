/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.google.common.collect.ImmutableList;
import com.wealdtech.Application;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;

/**
 * Interface defining message service methods
 */
public interface MessageService
{
  /**
   * Create a message
   */
  void create(Application app, WID<Topic> topicId, Message message);

  /**
   * Obtain messages for a given topic
   */
  ImmutableList<Message> obtain(Application app, WID<Topic> topicId);

  /**
   * Obtain a message given its ID
   */
  Message obtain(Application app, WID<Topic> topicId, WID<Message> messageId);

  /**
   * Obtain messages for a topic from a given user
   */
  ImmutableList<Message> obtainFrom(Application app, WID<Topic> topicId, WID<User> userId);

  /**
   * Obtain messages for a topic to a given user, either explicitly when they are named as a recipient or implicitly when the
   * message is sent to everyone
   */
  ImmutableList<Message> obtainTo(Application app, WID<Topic> topicId, WID<User> userId);
}
