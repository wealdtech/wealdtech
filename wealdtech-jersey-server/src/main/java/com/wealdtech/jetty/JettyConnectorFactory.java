package com.wealdtech.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.wealdtech.jetty.config.JettyConnectorConfiguration;

public interface JettyConnectorFactory
{
  Connector build(final JettyConnectorConfiguration config, final SslContextFactory sslContextFactory);
}
