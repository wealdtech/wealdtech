/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.services;

import com.wealdtech.WID;
import com.wealdtech.Application;
import com.wealdtech.chat.Message;
import com.wealdtech.chat.Topic;

/**
 * The public face of the Chat service.
 * This service provides all of the user-accessible methods for the chat service.  It handles authorisation and all of the
 * relationships between the lower-level services to ensure a single consistent view of the chat service.
 */
public interface ChatService
{
  void createTopic(Application app, Topic topic);
  void updateTopic(Application app, Topic topic);
  Topic obtainTopic(Application app, WID<Topic> topicId);
  void removeTopic(Application app, Topic topic);

  void createMessage(Application app, WID<Topic> topicId, Message message);
  Message obtainMessage(Application app, WID<Topic> topicId, WID<Message> messageId);
}
