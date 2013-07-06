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

package com.wealdtech.jetty;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;
import com.codahale.metrics.servlets.AdminServlet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.jersey.filters.BodyPrefetchFilter;
import com.wealdtech.jersey.filters.ThreadNameFilter;
import com.wealdtech.jetty.JettyServerConfiguration.ConnectorConfiguration;
import com.wealdtech.jetty.JettyServerConfiguration.SslConfiguration;
import com.wealdtech.jetty.JettyServerConfiguration.ThreadPoolConfiguration;
import com.wealdtech.utils.WealdMetrics;

/**
 * JettyServer sets up a Jetty server.
 * <p>
 * Configuration of the Jetty server is through a configuration object
 */
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

    this.server = new Server(createThreadPool());

    final SslContextFactory sslContextFactory = createSslContextFactory(configuration.getSslConfiguration());
    this.server.setConnectors(createConnectors(configuration.getConnectorConfigurations(), sslContextFactory));

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
    root.addFilter(GuiceFilter.class, "/*", null);
    root.addFilter(ThreadNameFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    root.addFilter(BodyPrefetchFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
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
   * Create the SSL context factory, used for secure connections
   */
  public SslContextFactory createSslContextFactory(final SslConfiguration configuration)
  {
    final SslContextFactory sslFactory = new SslContextFactory();
    sslFactory.setKeyStorePath(configuration.getKeyStorePath());
    sslFactory.setKeyStorePassword(configuration.getKeyStorePassword());
    sslFactory.setKeyManagerPassword(configuration.getKeyManagerPassword());
    return sslFactory;
  }

  /**
   * Create the connectors for a server
   */
  private Connector[] createConnectors(final ImmutableList<ConnectorConfiguration> configurations, final SslContextFactory sslFactory)
  {
    List<Connector> connectors = Lists.newArrayList();

    for (final ConnectorConfiguration configuration : configurations)
    {
      connectors.add(createConnector(configuration, sslFactory));
    }
    return connectors.toArray(new Connector[0]);
  }

  /**
   * Create a connector for a server
   */
  private Connector createConnector(final ConnectorConfiguration configuration, final SslContextFactory sslFactory)
  {
    // Fetch the connector
    Class<? extends JettyConnector> connectorClass = configuration.getType();
    final HttpConfiguration httpConfig = createHttpConfiguration(configuration);
    SslConnectionFactory sslConnectionFactory = null;

    // Specific configuration for secure/insecure connectors
    if (!configuration.isSecure())
    {
      // TODO httpConfig.setSecurePort();
      httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());
    }
    else
    {
      sslConnectionFactory = new SslConnectionFactory(sslFactory, HttpVersion.HTTP_1_1.asString());
      httpConfig.addCustomizer(new SecureRequestCustomizer());
    }
    final HttpConnectionFactory httpFactory = createConnectionFactory(configuration, httpConfig);
    final ServerConnector connector = new ServerConnector(this.server, sslConnectionFactory, httpFactory);
    // Common configuration
    connector.setName(configuration.getName());
    connector.setPort(configuration.getPort());
    connector.setHost(configuration.getHost());
    connector.setIdleTimeout(configuration.getIdleTimeout());
    connector.setAcceptQueueSize(configuration.getAcceptQueueSize());
    connector.setReuseAddress(configuration.getReuseAddress());
    connector.setSoLingerTime(configuration.getSoLingerTime());

    LOGGER.debug("Connector listening on port {}", configuration.getPort());
    return connector;
  }

  private HttpConfiguration createHttpConfiguration(final ConnectorConfiguration configuration)
  {
    final HttpConfiguration httpConfig = new HttpConfiguration();
    // TODO configuration parameters
    return httpConfig;
  }

  private HttpConnectionFactory createConnectionFactory(final ConnectorConfiguration configuration, final HttpConfiguration httpConfig)
  {
    final HttpConnectionFactory httpFactory = new HttpConnectionFactory(httpConfig);
    httpFactory.setInputBufferSize(configuration.getInputBufferSize());
    return httpFactory;
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