package com.wealdtech.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.wealdtech.jetty.config.JettyConnectorConfiguration;

public interface JettyConnectorFactory
{
  ServerConnector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration config, final SslContextFactory sslContextFactory);
}
