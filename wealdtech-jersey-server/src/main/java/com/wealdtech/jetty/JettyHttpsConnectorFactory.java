package com.wealdtech.jetty;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.wealdtech.jetty.config.JettyConnectorConfiguration;

public class JettyHttpsConnectorFactory extends JettyHttpConnectorFactory
{
  @Override
  public Connector build(final Server server, final ThreadPool threadPool, final JettyConnectorConfiguration configuration, final SslContextFactory sslContextFactory)
  {
    final Connector connector = super.build(server, threadPool, configuration, sslContextFactory);
  }

  public JettyHttpsConnectorFactory()
  {
    super();
  }

  private HttpConfiguration buildHttpConfiguration()
  {
    final HttpConfiguration httpConfig = super.buildHttpConfiguration();
    httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());

    return httpConfig;
  }
}
