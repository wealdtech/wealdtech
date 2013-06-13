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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;
import com.codahale.metrics.servlets.AdminServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.http.JettyServerConfiguration.ThreadPoolConfiguration;
import com.wealdtech.jersey.filters.BodyPrefetchFilter;
import com.wealdtech.jersey.filters.ThreadNameFilter;
import com.wealdtech.utils.WealdMetrics;

public class JettyServer
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);

  private transient Server server;

  private final transient JettyServerConfiguration configuration;

  private final transient Injector injector;

  @Inject
  public JettyServer(final Injector injector, final JettyServerConfiguration configuration)
  {
    this.injector = injector;
    this.configuration = configuration;

    final int port = this.configuration.getPort();
    LOGGER.info("Starting http server on port {}", port);
    this.server = new Server(createThreadPool());

    setConnectors();

    HandlerCollection handlers = new HandlerCollection();

    final ServletContextHandler admin = new ServletContextHandler();
    admin.setContextPath("/admin");
    admin.addServlet(AdminServlet.class, "/*");
    handlers.addHandler(admin);

    final ServletContextHandler root = new ServletContextHandler();
    root.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return JettyServer.this.injector;
      }
    });
    root.addFilter(ThreadNameFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    root.addFilter(BodyPrefetchFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    root.addFilter(GuiceFilter.class, "/*", null);
    root.addServlet(DefaultServlet.class, "/");
    handlers.addHandler(root);

    this.server.setHandler(handlers);
  }

  public void start() throws Exception // NOPMD
  {
    this.server.start();
  }

  public boolean isRunning()
  {
    return ((this.server != null) && (this.server.isRunning()));
  }

  public void join() throws Exception
  {
    if (this.server != null)
    {
      this.server.join();
    }
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
   * Set the connectors for a server
   */
  private void setConnectors()
  {
    final ServerConnector connector;

    // TODO Handle configuration
//    final ConnectorConfiguration connectorConfiguration = this.configuration.getConnectorConfiguration();

    connector = new ServerConnector(this.server);
    connector.setPort(this.configuration.getPort());
    this.server.setConnectors(new Connector[]{connector});
    // Blocking connector
//    connector = new InstrumentedBlockingChannelConnector(this.configuration.getPort());

//    if (connector instanceof SelectChannelConnector)
//    {
//      ((SelectChannelConnector)connector).setLowResourcesConnections(connectorConfiguration.getLowResourcesConnections());
//    }
//
//    if (connector instanceof AbstractNIOConnector)
//    {
//      ((AbstractNIOConnector)connector).setUseDirectBuffers(connectorConfiguration.getUseDirectBuffers());
//    }

//    connector.setAcceptors(connectorConfiguration.getAcceptors());
//
//    connector.setAcceptQueueSize(connectorConfiguration.getAcceptQueueSize());

//    return connector;
  }

  /**
   * Create a thread pool.
   * @return The thread pool
   */
  private ThreadPool createThreadPool()
  {
    final InstrumentedQueuedThreadPool pool = new InstrumentedQueuedThreadPool(WealdMetrics.defaultRegistry());

    final ThreadPoolConfiguration threadPoolConfiguration = this.configuration.getThreadPoolConfiguration();

    pool.setMinThreads(threadPoolConfiguration.getMinThreads());
    pool.setIdleTimeout(threadPoolConfiguration.getMaxIdleTimeMs());
//    pool.setMaxIdleTimeMs(threadPoolConfiguration.getMaxIdleTimeMs());
    return pool;
  }


  public void registerHandler(final String path, final Class<? extends Servlet> klazz)
  {
    // TODO add rather than overwrite
    final ServletContextHandler context = new ServletContextHandler();
    context.setContextPath(path);
    context.addServlet(klazz, "/*");
    this.server.setHandler(context);
  }
}