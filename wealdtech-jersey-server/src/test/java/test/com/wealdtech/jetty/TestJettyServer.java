/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jetty;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;
import test.com.wealdtech.config.ApplicationModule;

/**
 * A simple Jetty container to test Weald Jetty and Jersey servers
 */
public class TestJettyServer
{
  public static void main(final String[] args) throws Exception
  {
    // Create an injector with our basic configuration
    final Injector injector = Guice.createInjector(new ApplicationModule("config-multi.json"),
                                                   new JerseyServletModule(ImmutableList.<Class<? extends ContainerRequestFilter>>of(),
                                                                           ImmutableList.<Class<? extends ContainerResponseFilter>>of(),
                                                                           ImmutableList.of("test.com.wealdtech.jersey.resources")));
    final JettyServer server = injector.getInstance(JettyServer.class);
    server.start();
    server.join();
  }
}
