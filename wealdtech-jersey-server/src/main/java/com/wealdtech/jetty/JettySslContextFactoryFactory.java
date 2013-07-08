/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.jetty;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.wealdtech.jetty.config.JettySslConfiguration;

/**
 * A factory to create SSL context factories for secure Jetty connectors.
 *
 * @see SslContextFactory
 */
public class JettySslContextFactoryFactory
{
  public static SslContextFactory build(final JettySslConfiguration configuration)
  {
    final SslContextFactory factory = new SslContextFactory();

    factory.setKeyStorePath(configuration.getKeyStorePath());
    factory.setKeyStorePassword(configuration.getKeyStorePassword());
    factory.setKeyManagerPassword(configuration.getKeyManagerPassword());
    factory.setRenegotiationAllowed(configuration.isRenegotiationAllowed());
    factory.setSessionCachingEnabled(configuration.isSessionCachingEnabled());
    factory.setSslSessionCacheSize(configuration.getSessionCacheSize());
    factory.setSslSessionTimeout(configuration.getSessionTimeout());

    return factory;
  }
}
