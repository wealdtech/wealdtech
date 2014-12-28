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
import com.wealdtech.chat.Chat;

import javax.annotation.Nullable;

/**
 * Interface defining chat service methods
 */
public interface ChatService
{
  /**
   * Create the chat service's datastore.
   * If the datastore already exists then this does nothing; any data which was previously in the datastore will still be there
   * after this call completes.
   */
  public void createDatastore();

  /**
   * Destroy the chat service's datastore.
   * This is a destructive operation; when this is called it is expected that any existing data for the chat service will be
   * removed
   */
  public void destroyDatastore();

  /**
   * Add a chat to a topic
   */
  public void addChat(Chat chat);

  /**
   * Get chats
   */
  public ImmutableList<Chat> getChats(String from, @Nullable String topic);
}
