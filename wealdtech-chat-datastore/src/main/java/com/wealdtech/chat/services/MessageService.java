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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WID;
import com.wealdtech.chat.Application;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;
import com.wealdtech.chat.User;

/**
 * Interface defining message service methods
 */
public interface MessageService
{
  /**
   * Create a message
   */
  void create(WID<Application> appId, WID<Topic> topicId, Message message);

  /**
   * Obtain messages
   */
  ImmutableList<Message> obtain(WID<Application> appId, WID<Topic> topicId);

  /**
   * Obtain message
   */
  Message obtain(WID<Application> appId, WID<Topic> topicId, WID<Message> messageId);

  /**
   * Obtain messages
   */
  ImmutableList<Message> obtain(WID<Application> appId, WID<Topic> topicId, ImmutableCollection<WID<User>> userIds);
}
