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

import com.wealdtech.jetty.config.JettyConnectorConfiguration;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * A Jetty connector factory using the HTTPS protocol and building on the HTTP connector.
 *
 * @see ServerConnector
 */
public class JettyHttpsConnectorFactory extends JettyHttpConnectorFactory
{
  @Override
  public ServerConnector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration configuration, final SslContextFactory sslContextFactory)
  {
    // Start off building the HTTP connection
    final HttpConfiguration httpConfig = buildHttpConfiguration(configuration);
    final HttpConnectionFactory httpConnectionFactory = buildHttpConnectionFactory(httpConfig, configuration);

    // Add scheduler and buffer pool
    final Scheduler scheduler = new ScheduledExecutorScheduler();
    final ByteBufferPool bufferPool = buildBufferPool(configuration);

    // Set up the SSL connection factory
    final SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString());

//    // Instrument the connections
//    final String timerName = MetricRegistry.name(HttpConnectionFactory.class, configuration.getBindHost(), Integer.toString(configuration.getPort()), "connections");
//    final ConnectionFactory instrumentedConnectionFactory = new InstrumentedConnectionFactory(httpConnectionFactory, WealdMetrics.getMetricRegistry().timer(timerName));
//    // And create the connection itself
//    return buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, sslConnectionFactory, instrumentedConnectionFactory);
        return buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, sslConnectionFactory, httpConnectionFactory);
  }

  @Override
  protected ServerConnector buildConnector(final Server server, final ThreadPool threadPool, final Scheduler scheduler, final ByteBufferPool bufferPool, final String name, final JettyConnectorConfiguration configuration, final ConnectionFactory... connectionFactories)
  {
    final ServerConnector connector = super.buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, connectionFactories);
    super.setConnectorProperties(connector, name, configuration);
    return connector;
  }

  @Override
  protected HttpConfiguration buildHttpConfiguration(final JettyConnectorConfiguration configuration)
  {
    final HttpConfiguration httpConfig = super.buildHttpConfiguration(configuration);
    httpConfig.addCustomizer(new SecureRequestCustomizer());
    return httpConfig;
  }
}
