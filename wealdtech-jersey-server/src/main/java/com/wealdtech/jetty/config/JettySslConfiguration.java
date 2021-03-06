/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jetty.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Configuration for Jetty SSL.
 * <p>
 * The SSL confiugration requires information about the keystore and associated passwords
 */
public final class JettySslConfiguration implements Configuration
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettySslConfiguration.class);

  private String keyStorePath = "/etc/keystore";
  private String keyStorePasswd = "password";
  private String keyManagerPasswd = "password";
  private boolean renegotiationAllowed = true;
  private boolean sessionCachingEnabled = true;
  private int sessionCacheSize = 1024;
  private int sessionTimeout = 120;

  public JettySslConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettySslConfiguration(@JsonProperty("keystorepath") final String keyStorePath,
                                @JsonProperty("keystorepassword") final String keyStorePasswd,
                                @JsonProperty("keymanagerpassword") final String keyManagerPasswd,
                                @JsonProperty("renegotiationallowed") final Boolean renegotiationAllowed,
                                @JsonProperty("sessioncachingenabled") final Boolean sessionCachingEnabled,
                                @JsonProperty("sessioncachesize") final Integer sessionCacheSize,
                                @JsonProperty("sessiontimeout") final Integer sessionTimeout)
  {
    this.keyStorePath = resolvePath(MoreObjects.firstNonNull(keyStorePath, this.keyStorePath));
    this.keyStorePasswd = MoreObjects.firstNonNull(keyStorePasswd, this.keyStorePasswd);
    this.keyManagerPasswd = MoreObjects.firstNonNull(keyManagerPasswd, this.keyManagerPasswd);
    this.renegotiationAllowed = MoreObjects.firstNonNull(renegotiationAllowed, this.renegotiationAllowed);
    this.sessionCachingEnabled = MoreObjects.firstNonNull(sessionCachingEnabled, this.sessionCachingEnabled);
    this.sessionCacheSize = MoreObjects.firstNonNull(sessionCacheSize, this.sessionCacheSize);
    this.sessionTimeout = MoreObjects.firstNonNull(sessionTimeout, this.sessionTimeout);
  }

  private String resolvePath(final String input)
  {
    String result = input;
    if (!input.startsWith("/"))
    {
      // This is a relative path so look for the file in our resources
      final URL resourceUrl = ResourceLoader.getResource(input);
      if (resourceUrl != null)
      {
        result = resourceUrl.getPath();
      }
      else
      {
        // Try a simple path
        result = System.getProperty("user.home") + "/" + input;
      }
    }
    LOGGER.debug("Resolved path from \"{}\" to \"{}\"", input, result);
    return result;
  }

  public String getKeyStorePath()
  {
    return this.keyStorePath;
  }

  public String getKeyStorePassword()
  {
    return this.keyStorePasswd;
  }

  public String getKeyManagerPassword()
  {
    return this.keyManagerPasswd;
  }

  public boolean isRenegotiationAllowed()
  {
    return this.renegotiationAllowed;
  }

  public boolean isSessionCachingEnabled()
  {
    return this.sessionCachingEnabled;
  }

  public int getSessionCacheSize()
  {
    return this.sessionCacheSize;
  }

  public int getSessionTimeout()
  {
    return this.sessionTimeout;
  }
}
