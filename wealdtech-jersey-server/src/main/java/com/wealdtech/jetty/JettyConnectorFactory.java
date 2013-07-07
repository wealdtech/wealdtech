package com.wealdtech.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.wealdtech.jetty.config.JettyConnectorConfiguration;

public interface JettyConnectorFactory
{
  Connector build(final Server server, final ThreadPool threadPool, final String name, final JettyConnectorConfiguration config, final SslContextFactory sslContextFactory);
}
