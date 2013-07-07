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

    // FIXME configuration
//    factory.setRenegotiationAllowed(true);
//    factory.setIncludeProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3"});
//    factory.setIncludeCipherSuites(new String[] {"TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA256"});

    return factory;
  }
}
