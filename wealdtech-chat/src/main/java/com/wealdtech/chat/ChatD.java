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
import com.wealdtech.config.WealdInstrumentationModule;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;

/**
 * Daemon for the chat API
 */
public class ChatD
{
  public static void main(final String[] args)
  {
    final Injector injector = Guice.createInjector(new ApplicationModule("chatd-config.json"),
                                                   new WealdInstrumentationModule(),
                                                   new JerseyServletModule("com.wealdtech.chat.resources"));
    final JettyServer server = injector.getInstance(JettyServer.class);

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
