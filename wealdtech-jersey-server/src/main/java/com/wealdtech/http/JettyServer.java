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

package com.wealdtech.http;

import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.AbstractNIOConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.http.JettyServerConfiguration.ConnectorConfiguration;
import com.wealdtech.http.JettyServerConfiguration.ThreadPoolConfiguration;
import com.yammer.metrics.jetty.InstrumentedBlockingChannelConnector;
import com.yammer.metrics.jetty.InstrumentedQueuedThreadPool;
import com.yammer.metrics.reporting.AdminServlet;

public class JettyServer
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);

  private transient Server server;

  private transient final JettyServerConfiguration configuration;

  private transient final Injector injector;

  @Inject
  public JettyServer(final Injector injector, final JettyServerConfiguration configuration)
  {
    this.injector = injector;
    this.configuration = configuration;
  }

  public void start() throws Exception // NOPMD
  {
    final int port = this.configuration.getPort();
    LOGGER.info("Starting http server on port {}", port);
    this.server = new Server();

    this.server.addConnector(createConnector());
    this.server.setThreadPool(createThreadPool());

    final ServletContextHandler admin = new ServletContextHandler(this.server, "/admin", ServletContextHandler.SESSIONS);
    admin.addServlet(AdminServlet.class, "/*");

    final ServletContextHandler root = new ServletContextHandler(this.server, "/", ServletContextHandler.SESSIONS);
    root.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return JettyServer.this.injector;
      }
    });
    root.addFilter(GuiceFilter.class, "/*", null);
    root.addServlet(DefaultServlet.class, "/");

    this.server.start();
    this.server.join();
  }

  public boolean isRunning()
  {
    return ((this.server != null) && (this.server.isRunning()));
  }

  public void stop() throws Exception // NOPMD
  {
    if (this.server != null)
    {
      this.server.stop();
    }
  }

  public void waitForShutdown() throws InterruptedException
  {
    if (this.server != null)
    {
      this.server.join();
    }
  }

  /**
   * Create a connector
   * @return The connector
   */
  private AbstractConnector createConnector()
  {
    final AbstractConnector connector;

    ConnectorConfiguration configuration = this.configuration.getConnector();

    // Blocking connector
    connector = new InstrumentedBlockingChannelConnector(this.configuration.getPort());

    if (connector instanceof SelectChannelConnector)
    {
      ((SelectChannelConnector)connector).setLowResourcesConnections(configuration.getLowResourcesConnections());
    }

    if (connector instanceof AbstractNIOConnector)
    {
      ((AbstractNIOConnector)connector).setUseDirectBuffers(configuration.getUseDirectBuffers());
    }

    connector.setAcceptors(configuration.getAcceptors());

    connector.setAcceptQueueSize(configuration.getAcceptQueueSize());

    return connector;
  }

  /**
   * Create a thread pool.
   * @return The thread pool
   */
  private ThreadPool createThreadPool()
  {
    final InstrumentedQueuedThreadPool pool = new InstrumentedQueuedThreadPool();
    ThreadPoolConfiguration configuration = this.configuration.getThreadPool();
    pool.setMinThreads(configuration.getMinThreads());
    pool.setMaxIdleTimeMs(configuration.getMaxIdleTimeMs());
    return pool;
  }
}