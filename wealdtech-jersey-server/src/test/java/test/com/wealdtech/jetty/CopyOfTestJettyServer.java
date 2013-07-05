/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package test.com.wealdtech.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.com.wealdtech.config.ApplicationModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.jetty.JettyServer;

/**
 * A simple Jetty container to test Weald Jetty and Jersey changes
 */
public class CopyOfTestJettyServer
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CopyOfTestJettyServer.class);

  public static void main(final String[] args) throws Exception
  {
    // Create an injector with our basic configuration
    final Injector injector = Guice.createInjector(new ApplicationModule("config-multi.json"));
    final JettyServer webserver = injector.getInstance(JettyServer.class);
    webserver.start();
    webserver.join();
  }
}
