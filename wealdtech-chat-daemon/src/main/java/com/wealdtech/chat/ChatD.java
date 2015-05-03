/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.chat.config.ApplicationModule;
import com.wealdtech.chat.listeners.MessageListener;
import com.wealdtech.config.WealdInstrumentationModule;
import com.wealdtech.guice.EventBusModule;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;

/**
 * Daemon for the chat API.
 * The chat daemon has three endpoints: topics, messages and subscriptions.
 * A user can subscribe to any number of topics.
 * A user can send a message to any topic to which they are subscribed.
 * A user can send a message to any set of individuals who are also subscribed to the same topic.
 * A user can retrieve messages from any topic to which they are subscribed.
 */
public class ChatD
{
  public static void main(final String[] args)
  {
    final Injector injector = Guice.createInjector(new ApplicationModule("chatd-config.json"),
                                                   new WealdInstrumentationModule(),
                                                   new EventBusModule(),
                                                   new JerseyServletModule("com.wealdtech.chat.resources"));
    final JettyServer server = injector.getInstance(JettyServer.class);

    // Inject our listeners
    injector.getInstance(MessageListener.class);

    try
    {
      server.start();
    }
    catch (final Exception e)
    {
      System.err.println(e);
      System.exit(0);
    }
  }
}
