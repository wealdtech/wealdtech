/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jetty;

import com.codahale.metrics.servlets.AdminServlet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.DataError;
import com.wealdtech.jersey.filters.BodyPrefetchFilter;
import com.wealdtech.jersey.filters.ThreadNameFilter;
import com.wealdtech.jetty.config.JettyConnectorConfiguration;
import com.wealdtech.jetty.config.JettyInstanceConfiguration;
import com.wealdtech.jetty.config.JettyServerConfiguration;
import com.wealdtech.jetty.config.MetricsServletContextListener;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * JettyServer sets up a Jetty server.
 * <p>
 * Configuration of the Jetty server is through a configuration object
 */
public class JettyServer
{
  private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

  private transient Server server;

  private final transient Injector injector;

  /**
   * Create a Jetty server with a specific configuration
   * @param injector a Guice injector
   * @param configuration configuration for the Jetty server
   */
  @Inject
  public JettyServer(final Injector injector, final JettyServerConfiguration configuration)
  {
    // Needed to access the injector
    this.injector = injector;

    // Create the server and set up the instances
    this.server = new Server();
    final List<Connector> connectors = Lists.newArrayList();
    for (final JettyInstanceConfiguration instanceConfiguration : configuration.getInstanceConfigurations())
    {
      connectors.addAll(configureInstance(this.server, instanceConfiguration));
    }
    this.server.setConnectors(connectors.toArray(new Connector[0]));

    // Add the handlers
    this.server.setHandler(createHandlers(configuration));
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

  private List<Connector> configureInstance(final Server server, final JettyInstanceConfiguration configuration)
  {
    // We have a single SSL context factory and thread pool for each instance
    final SslContextFactory sslContextFactory = JettySslContextFactoryFactory.build(configuration.getSslConfiguration());
    final ThreadPool threadPool = JettyThreadPoolFactory.build(configuration.getName(), configuration.getThreadPoolConfiguration());

    // Create each connector
    final List<Connector> connectors = Lists.newArrayList();
    for (final JettyConnectorConfiguration connectorConfiguration : configuration.getConnectorConfigurations())
    {
      LOG.debug("Creating connector {}:{} for instance \"{}\"", connectorConfiguration.getBindHost(),
                   connectorConfiguration.getPort(), configuration.getName());
      JettyConnectorFactory factory;
      try
      {
        final Constructor<? extends JettyConnectorFactory> ctor = connectorConfiguration.getType().getDeclaredConstructor();
        ctor.setAccessible(true);
        factory = ctor.newInstance();
      }
      catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
      {
        throw new DataError.Bad("Failed to set up connector type \"" + connectorConfiguration.getType() + "\"");
      }
      connectors.add(factory.build(server, threadPool, configuration.getName(), connectorConfiguration, sslContextFactory));
    }
    return connectors;
  }

  /**
   * This sets the basic handlers, including filters, to allow for Guice injection in to servlets
   *
   * @return A handler collection containing the basic handlers
   */
  private HandlerCollection createHandlers(final JettyServerConfiguration configuration)
  {
    final HandlerCollection handlers = new HandlerCollection();

    final ServletContextHandler admin = new ServletContextHandler();
    admin.addEventListener(new MetricsServletContextListener());
    admin.setContextPath(configuration.getMetricsEndpoint());
    admin.addServlet(AdminServlet.class, "/*");
    handlers.addHandler(admin);

    handlers.addHandler(new ShutdownHandler(configuration.getShutdownToken()));

    final ServletContextHandler root = new ServletContextHandler();
    root.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return JettyServer.this.injector;
      }
    });
    if (configuration.getDetailedThreadName())
    {
      LOG.info("Adding thread name filter");
      root.addFilter(ThreadNameFilter.class, "/*", null);
    }
    else
    {
      LOG.info("Not adding thread name filter");
    }
    if (configuration.getBodyPrefetch())
    {
      LOG.info("Adding body prefetch filter");
      root.addFilter(BodyPrefetchFilter.class, "/*", null);
    }
    else
    {
      LOG.info("Not adding body prefetch filter");
    }
    root.addFilter(GuiceFilter.class, "/*", null);
    root.addServlet(DefaultServlet.class, "/");
    handlers.addHandler(root);

    return handlers;
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