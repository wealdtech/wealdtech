package com.wealdtech.jetty;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;

public class JettyHttpsConnectorFactory extends JettyHttpConnectorFactory
{
  private HttpConfiguration httpConfig;

  @Override
  public Connector build(final Server server)
  {
    final Connector connector = super.build(server);
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
