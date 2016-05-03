/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.wealdtech.config.WealdInstrumentationModule;
import com.wealdtech.jersey.filters.*;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;
import com.wealdtech.nlp.config.NlpDConfiguration;
import com.wealdtech.nlp.config.NlpDModule;

/**
 *
 */
public class NlpD
{
  public static void main(final String[] args)
  {
    final Injector injector =
        Guice.createInjector(new NlpDModule("nlpd-config.json"), new WealdInstrumentationModule(),
                             new JerseyServletModule(ImmutableList.of(RequestLoggingFilter.class, RequestHintFilter.class,
                                                                      DeviceIdFilter.class, GZIPContentEncodingFilter.class),
                                                     ImmutableList.of(RequestLoggingFilter.class, ServerHeaderFilter.class,
                                                                      CORSFilter.class, GZIPContentEncodingFilter.class),
                                                     ImmutableList.of("com.wealdtech.nlp.resources")));
    final JettyServer server = injector.getInstance(JettyServer.class);

    Logging.setLogging(injector.getInstance(NlpDConfiguration.class).getLoggingConfiguration());

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
