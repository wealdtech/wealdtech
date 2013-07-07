package com.wealdtech.jetty;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
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

public class JettyHttpConnectorFactory implements JettyConnectorFactory
{
  public JettyHttpConnectorFactory()
  {
  }

  @Override
  public Connector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration configuration, final SslContextFactory sslContextFactory)
  {
    // Start off building the HTTP connection
    final HttpConfiguration httpConfig = buildHttpConfiguration();
    final HttpConnectionFactory httpConnectionFactory = buildHttpConnectionFactory(httpConfig);

    // Add scheduler and buffer pool
    final Scheduler scheduler = new ScheduledExecutorScheduler();
    final ByteBufferPool bufferPool = buildBufferPool(configuration);

    // Instrument the connections
    final String timerName = MetricRegistry.name(HttpConnectionFactory.class, configuration.getBindHost(), Integer.toString(configuration.getPort()), "connections");
    final ConnectionFactory instrumentedConnectionFactory = new InstrumentedConnectionFactory(httpConnectionFactory, WealdMetrics.defaultRegistry().timer(timerName));
    // And create the connection itself
    return buildConnector(server, threadPool, scheduler, bufferPool, name, configuration, instrumentedConnectionFactory);
  }

  protected HttpConfiguration buildHttpConfiguration()
  {
    final HttpConfiguration httpConfig = new HttpConfiguration();
    // FIXME configuration variables
//    httpConfig.setHeaderCacheSize(headerCacheSize);
//    httpConfig.setOutputBufferSize(outputBufferSize);
//    httpConfig.setRequestHeaderSize(maxRequestHeaderSize);
//    httpConfig.setResponseHeaderSize(maxResponseHeaderSize);
//    httpConfig.setSendDateHeader(useDateHeader);
//    httpConfig.setSendServerVersion(useServerHeader);
//    if (useForwardedHeaders)
//    {
//      httpConfig.addCustomizer(new ForwardedRequestCustomizer());
//    }
    httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());
    return httpConfig;
  }

  protected HttpConnectionFactory buildHttpConnectionFactory(final HttpConfiguration httpConfig)
  {
    // FIXME configuration
    return new HttpConnectionFactory(httpConfig);
  }

  protected Connector buildConnector(final Server server, final ThreadPool threadPool, final Scheduler scheduler, final ByteBufferPool bufferPool, final String name, final JettyConnectorConfiguration configuration, final ConnectionFactory... connectionFactories)
  {
    // FIXME configuration parameters
    final ServerConnector connector = new ServerConnector(server, threadPool, scheduler, bufferPool, 0, 0, connectionFactories);

    connector.setName(name);
    connector.setPort(configuration.getPort());
    connector.setHost(configuration.getBindHost());
    connector.setIdleTimeout(configuration.getIdleTimeout());
    connector.setAcceptQueueSize(configuration.getAcceptQueueSize());
    connector.setReuseAddress(configuration.getReuseAddress());
    connector.setSoLingerTime(configuration.getSoLingerTime());
    return connector;
  }

  protected ByteBufferPool buildBufferPool(final JettyConnectorConfiguration configuration)
  {
    // FIXME configuration variables
    return new ArrayByteBufferPool(1024, 4096, 1048576);
//      return new ArrayByteBufferPool(minBufferPoolSize, bufferPoolIncrement, maxBufferPoolSize);
  }


}
