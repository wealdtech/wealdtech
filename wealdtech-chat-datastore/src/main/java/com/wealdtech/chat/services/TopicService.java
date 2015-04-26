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
import com.wealdtech.chat.Application;
import com.wealdtech.chat.Topic;

import javax.annotation.Nullable;

/**
 * Interface defining topic service methods
 */
public interface TopicService
{
  /**
   * Create a topic
   */
  void create(WID<Application> appId, Topic topic);

  /**
   * Update a topic
   */
  void update(WID<Application> appId, Topic topic);

  /**
   * Obtain a topic
   */
  @Nullable
  Topic obtain(WID<Application> appId, WID<Topic> topicId);
}
