package com.wealdtech.jetty;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import com.wealdtech.jetty.config.JettyConnectorConfiguration;
import com.wealdtech.utils.WealdMetrics;

public class JettyHttpsConnectorFactory extends JettyHttpConnectorFactory
{
  @Override
  public ServerConnector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration configuration, final SslContextFactory sslContextFactory)
  {
    // Start off building the HTTP connection
    final HttpConfiguration httpConfig = buildHttpConfiguration();
    final HttpConnectionFactory httpConnectionFactory = buildHttpConnectionFactory(httpConfig);

    // Add scheduler and buffer pool
    final Scheduler scheduler = new ScheduledExecutorScheduler();
    final ByteBufferPool bufferPool = buildBufferPool(configuration);

    // Set up the SSL connection factory
    final SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString());

    // Instrument the connections
    final String timerName = MetricRegistry.name(HttpConnectionFactory.class, configuration.getBindHost(), Integer.toString(configuration.getPort()), "connections");
    final ConnectionFactory instrumentedConnectionFactory = new InstrumentedConnectionFactory(httpConnectionFactory, WealdMetrics.defaultRegistry().timer(timerName));
    // And create the connection itself
    return buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, sslConnectionFactory, instrumentedConnectionFactory);
  }

  @Override
  protected ServerConnector buildConnector(final Server server, final ThreadPool threadPool, final Scheduler scheduler, final ByteBufferPool bufferPool, final String name, final JettyConnectorConfiguration configuration, final ConnectionFactory... connectionFactories)
  {
    final ServerConnector connector = super.buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, connectionFactories);
    super.setConnectorProperties(connector, name, configuration);
    return connector;
  }

  @Override
  protected HttpConfiguration buildHttpConfiguration()
  {
    final HttpConfiguration httpConfig = super.buildHttpConfiguration();
    httpConfig.addCustomizer(new SecureRequestCustomizer());
    return httpConfig;
  }
}
