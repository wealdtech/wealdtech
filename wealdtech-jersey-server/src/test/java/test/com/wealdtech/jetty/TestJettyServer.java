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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.http.JettyServer;
import com.wealdtech.jersey.guice.JerseyServletModule;

/**
 * A simple Jetty container to test Weald Jetty and Jersey changes
 */
public class TestJettyServer
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TestJettyServer.class);

  private transient Server server;

  private transient final Injector injector;

  @Inject
  public TestJettyServer(final Injector injector)
  {
    this.injector = injector;
  }

  public void start() throws Exception // NOPMD
  {
    final int port = 8080;
    LOGGER.info("Starting http server on port {}", port);
    this.server = new Server();

    final ServletContextHandler context = new ServletContextHandler(this.server, "/");
    context.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return TestJettyServer.this.injector;
      }
    });
    context.addFilter(GuiceFilter.class, "/*", null);
    context.addServlet(DefaultServlet.class, "/");

    this.server.start();
  }

  public void join() throws Exception
  {
    this.server.join();
  }

  public void stop() throws Exception
  {
    this.server.stop();
  }

  public static void main(final String[] args) throws Exception
  {
    // Create an injector with our basic configuration
    final Injector injector = Guice.createInjector(new JerseyServletModule("test.com.wealdtech.jersey.resources"));
    final JettyServer webserver = injector.getInstance(JettyServer.class);
    webserver.start();
    webserver.join();
  }
}
