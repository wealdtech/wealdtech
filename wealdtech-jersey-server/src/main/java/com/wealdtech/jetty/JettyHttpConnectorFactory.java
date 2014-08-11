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

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import com.wealdtech.jetty.config.JettyConnectorConfiguration;
import com.wealdtech.utils.WealdMetrics;

/**
 * A Jetty connector factory using the HTTP protocol.
 *
 * @see ServerConnector
 */
public class JettyHttpConnectorFactory implements JettyConnectorFactory
{
  public JettyHttpConnectorFactory()
  {
  }

  @Override
  public ServerConnector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration configuration, final SslContextFactory sslContextFactory)
  {
    // Start off building the HTTP connection
    final HttpConfiguration httpConfig = buildHttpConfiguration(configuration);
    final HttpConnectionFactory httpConnectionFactory = buildHttpConnectionFactory(httpConfig, configuration);

    // Add scheduler and buffer pool
    final Scheduler scheduler = new ScheduledExecutorScheduler();
    final ByteBufferPool bufferPool = buildBufferPool(configuration);

    // Instrument the connections
    final String timerName = MetricRegistry.name(HttpConnectionFactory.class, configuration.getBindHost(), Integer.toString(configuration.getPort()), "connections");
    final ConnectionFactory instrumentedConnectionFactory = new InstrumentedConnectionFactory(httpConnectionFactory, WealdMetrics.getMetricRegistry().timer(timerName));
    // And create the connection itself
    return buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, instrumentedConnectionFactory);
  }

  protected HttpConfiguration buildHttpConfiguration(final JettyConnectorConfiguration configuration)
  {
    final HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setHeaderCacheSize(configuration.getHeaderCacheSize());
    httpConfig.setOutputBufferSize(configuration.getOutputBufferSize());
    httpConfig.setRequestHeaderSize(configuration.getRequestHeaderSize());
    httpConfig.setResponseHeaderSize(configuration.getResponseHeaderSize());
    httpConfig.setSendDateHeader(configuration.getSendDateHeader());
    httpConfig.setSendServerVersion(configuration.getSendServerVersion());
    if (configuration.useForwardedHeaders())
    {
      httpConfig.addCustomizer(new ForwardedRequestCustomizer());
    }
    httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());
    return httpConfig;
  }

  protected HttpConnectionFactory buildHttpConnectionFactory(final HttpConfiguration httpConfig, final JettyConnectorConfiguration configuration)
  {
    final HttpConnectionFactory factory = new HttpConnectionFactory(httpConfig);
    factory.setInputBufferSize(configuration.getInputBufferSize());
    return factory;
  }

  protected ServerConnector buildConnector(final Server server, final ThreadPool threadPool, final Scheduler scheduler, final ByteBufferPool bufferPool, final String name, final JettyConnectorConfiguration configuration, final ConnectionFactory... connectionFactories)
  {
    final ServerConnector connector = new ServerConnector(server, threadPool, scheduler, bufferPool, configuration.getAcceptorThreads(), configuration.getSelectorThreads(), connectionFactories);
    setConnectorProperties(connector, name, configuration);
    return connector;
  }

  protected void setConnectorProperties(final ServerConnector connector, final String name, final JettyConnectorConfiguration configuration)
  {
    connector.setName(name);
    connector.setPort(configuration.getPort());
    connector.setHost(configuration.getBindHost());
    connector.setIdleTimeout(configuration.getIdleTimeout());
    connector.setAcceptQueueSize(configuration.getAcceptQueueSize());
    connector.setReuseAddress(configuration.getReuseAddress());
    connector.setSoLingerTime(configuration.getSoLingerTime());
  }

  protected ByteBufferPool buildBufferPool(final JettyConnectorConfiguration configuration)
  {
    return new ArrayByteBufferPool(configuration.getMinBufferPoolSize(), configuration.getBufferPoolIncrement(), configuration.getMaxBufferPoolSize());
  }


}
