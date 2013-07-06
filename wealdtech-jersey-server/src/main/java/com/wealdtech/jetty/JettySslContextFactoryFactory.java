package com.wealdtech.jetty;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.wealdtech.jetty.config.JettySslConfiguration;

public class JettySslContextFactoryFactory
{
  public static SslContextFactory build(final JettySslConfiguration configuration)
  {
    final SslContextFactory factory = new SslContextFactory();

    factory.setKeyStorePath(configuration.getKeyStorePath());
    factory.setKeyStorePassword(configuration.getKeyStorePassword());
    factory.setKeyManagerPassword(configuration.getKeyManagerPassword());

    return factory;
  }
}
