/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.logger.jersey.filters;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;
import org.testng.annotations.Test;

/**
 */
public class RequestLoggingFilterTest
{
  @Test
  public void testLogger()
  {
    // Set up our filter
    final Injector injector = Guice.createInjector(new TestApplicationModule(),
                                                   new JerseyServletModule(ImmutableList.<Class<? extends ContainerRequestFilter>>of(LoggerFilter.class),
                                                                           ImmutableList.<Class<? extends ContainerResponseFilter>>of(LoggerFilter.class),
                                                                           ImmutableList.of("com.wealdtech.test.resources")));
    final JettyServer server = injector.getInstance(JettyServer.class);

    try
    {
      server.start();
//      while (true)
//      {
//        Thread.sleep(1000l);
//      }
    }
    catch (final Exception ignored) {}

  }
}
