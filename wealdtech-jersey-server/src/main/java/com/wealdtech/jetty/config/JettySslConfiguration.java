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

package com.wealdtech.jetty.config;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.utils.ResourceLoader;

/**
 * Configuration for Jetty SSL.
 * <p>
 * The SSL confiugration requires information about the keystore and associated passwords
 */
public final class JettySslConfiguration implements Configuration
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettySslConfiguration.class);

  private String keystorepath = "/etc/keystore";
  private String keystorepasswd = "password";
  private String keymanagerpasswd = "password";

  public JettySslConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettySslConfiguration(@JsonProperty("keystorepath") final String keystorepath,
                                @JsonProperty("keystorepassword") final String keystorepasswd,
                                @JsonProperty("keymanagerpassword") final String keymanagerpasswd)
  {
    this.keystorepath = resolvePath(Objects.firstNonNull(keystorepath, this.keystorepath));
    this.keystorepasswd = Objects.firstNonNull(keystorepasswd, this.keystorepasswd);
    this.keymanagerpasswd = Objects.firstNonNull(keymanagerpasswd, this.keymanagerpasswd);
  }

  private String resolvePath(final String input)
  {
    String result = input;
    if (!input.startsWith("/"))
    {
      // This is a relative path so look for the file in our resources are
      final URL resourceUrl = ResourceLoader.getResource(input);
      if (resourceUrl != null)
      {
        result = resourceUrl.getPath();
      }
    }
    LOGGER.debug("Resolved path from \"{}\" to \"{}\"", input, result);
    return result;
  }

  public String getKeyStorePath()
  {
    return this.keystorepath;
  }

  public String getKeyStorePassword()
  {
    return this.keystorepasswd;
  }

  public String getKeyManagerPassword()
  {
    return this.keymanagerpasswd;
  }
}
