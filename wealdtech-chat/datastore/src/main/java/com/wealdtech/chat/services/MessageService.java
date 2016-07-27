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
import com.wealdtech.services.WObjectService;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Interface defining message service methods
 */
public interface MessageService<T> extends WObjectService<Message, T>
{
  /**
   * Create a message
   * @param app the application to which this message belongs
   * @param user the user creating the message
   * @param topic the topic to which this message belongs
   * @param message the message
   */
  void create(Application app, User user, Topic topic, Message message);

  /**
   * Obtain messages for a given topic
   * @param app the application to which this message belongs
   * @param user the user creating the message
   * @param topic the topic to which this message belongs
   * @return a list of messages for the given topic
   */
  ImmutableList<Message> obtain(Application app, User user, Topic topic);

  /**
   * Obtain a message given its ID
   * @param app the application to which this message belongs
   * @param user the user creating the message
   * @param topic the topic to which this message belongs
   * @param messageId the ID of the message to obtain
   * @return the message
   */
  Message obtain(Application app, User user, Topic topic, WID<Message> messageId);

  /**
   * Obtain messages for a topic from a given user
   * @param app the application to which this message belongs
   * @param user the user creating the message
   * @param topic the topic to which this message belongs
   * @param since the time since we want to obtain new message; can be {@code null} to obtain all messages
   * @return a list of messages from the given user
   */
  ImmutableList<Message> obtainFrom(Application app, User user, Topic topic, @Nullable DateTime since);

  /**
   * Obtain messages for a topic to a given user, either explicitly when they are named as a recipient or implicitly when the
   * message is sent to everyone
   * @param app the application to which this message belongs
   * @param user the user creating the message
   * @param topic the topic to which this message belongs
   * @param since the time since we want to obtain new message; can be {@code null} to obtain all messages
   * @return a list of messages to the given user
   */
  ImmutableList<Message> obtainTo(Application app, User user,  Topic topic, @Nullable DateTime since);
}
