package com.wealdtech.jetty;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.wealdtech.jetty.config.JettyServerConfiguration.ConnectorConfiguration;

public class JettyHttpConnectorFactory implements JettyConnectorFactory
{
  public JettyHttpConnectorFactory()
  {
  }

  @Override
  public Connector build(final Server server, final ThreadPool threadPool, final ConnectorConfiguration configuration)
  {
    // Start off building the HTTP connection
    final HttpConfiguration httpConfig = buildHttpConfiguration();
    final HttpConnectionFactory httpConnectionFactory = buildHttpConnectionFactory(httpConfig);

    final Scheduler scheduler = new ScheduledExecutorScheduler();

    final ByteBufferPool bufferPool = buildBufferPool();

    return buildConnector(server, scheduler, bufferPool, configuration.getName(), threadPool, null, httpConnectionFactory);
  }

  private HttpConfiguration buildHttpConfiguration()
  {
    final HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setHeaderCacheSize(headerCacheSize);
    httpConfig.setOutputBufferSize(outputBufferSize);
    httpConfig.setRequestHeaderSize(maxRequestHeaderSize);
    httpConfig.setResponseHeaderSize(maxResponseHeaderSize);
    httpConfig.setSendDateHeader(useDateHeader);
    httpConfig.setSendServerVersion(useServerHeader);
    if (useForwardedHeaders)
    {
      httpConfig.addCustomizer(new ForwardedRequestCustomizer());
    }
    return httpConfig;
    httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());
  }

  private HttpConnectionFactory buildHttpConnectionFactory(final HttpConfiguration httpConfig)
  {
    return null;
  }

  final ByteBufferPool buildBufferPool(final ConnectorConfiguration configuration)
  {
      return new ArrayByteBufferPool(minBufferPoolSize, bufferPoolIncrement, maxBufferPoolSize);
  }

  final ServerConnector connector = new ServerConnector(server, sslConnectionFactory, httpFactory);
    // Common configuration
    connector.setName(this.configuration.getName());
    connector.setPort(this.configuration.getPort());
    connector.setHost(this.configuration.getHost());
    connector.setIdleTimeout(this.configuration.getIdleTimeout());
    connector.setAcceptQueueSize(this.configuration.getAcceptQueueSize());
    connector.setReuseAddress(this.configuration.getReuseAddress());
    connector.setSoLingerTime(this.configuration.getSoLingerTime());
    return connector;
  }

}
