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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.jetty.JettyConnectorFactory;
import com.wealdtech.jetty.JettyHttpConnectorFactory;
import com.wealdtech.jetty.JettyHttpsConnectorFactory;

/**
 * Configuration for a Jetty connector.
 * <p>
 */
public final class JettyConnectorConfiguration implements Configuration
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettyConnectorConfiguration.class);

  private String bindhost = "localhost";
  private int port = 8080;
  private Class<? extends JettyConnectorFactory> type = JettyHttpConnectorFactory.class;
  private int acceptqueuesize = -1;
  private long idletimeout = 50000;
  private boolean reuseaddress = false;
  private int solingertime = 60;
  private int inputbuffersize = 2048;

  public JettyConnectorConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettyConnectorConfiguration(@JsonProperty("bindhost") final String bindhost,
                                      @JsonProperty("port") final Integer port,
                                      @JsonProperty("type") final String type,
                                      @JsonProperty("acceptqueuesize") final Integer acceptqueuesize,
                                      @JsonProperty("idletimeout") final Long idletimeout,
                                      @JsonProperty("reuseaddress") final Boolean reuseaddress,
                                      @JsonProperty("solingertime") final Integer solingertime,
                                      @JsonProperty("inputbuffersize") final Integer inputbuffersize)
  {
    this.bindhost = Objects.firstNonNull(bindhost, this.bindhost);
    this.port = Objects.firstNonNull(port, this.port);
    this.type = Objects.firstNonNull(classFromType(type), this.type);
    this.acceptqueuesize = Objects.firstNonNull(acceptqueuesize, this.acceptqueuesize);
    this.idletimeout = Objects.firstNonNull(idletimeout, this.idletimeout);
    this.reuseaddress = Objects.firstNonNull(reuseaddress, this.reuseaddress);
    this.solingertime = Objects.firstNonNull(solingertime, this.solingertime);
    this.inputbuffersize = Objects.firstNonNull(inputbuffersize, this.inputbuffersize);
  }

  public String getBindHost()
  {
    return this.bindhost;
  }

  public int getPort()
  {
    return this.port;
  }

  public Class<? extends JettyConnectorFactory> getType()
  {
    return this.type;
  }

  public int getAcceptQueueSize()
  {
    return this.acceptqueuesize;
  }

  public long getIdleTimeout()
  {
    return this.idletimeout;
  }

  public boolean getReuseAddress()
  {
    return this.reuseaddress;
  }

  public int getSoLingerTime()
  {
    return this.solingertime;
  }

  public int getInputBufferSize()
  {
    return this.inputbuffersize;
  }

  /**
   * Obtain a Jetty connector given a type name.
   */
  private Class<? extends JettyConnectorFactory> classFromType(final String type)
  {
    Class<? extends JettyConnectorFactory> klazz = null;
    if (type != null)
    {
      switch (type.toLowerCase())
      {
        case "http":
          klazz = JettyHttpConnectorFactory.class;
          break;
        case "https":
          klazz =  JettyHttpsConnectorFactory.class;
          break;
        default:
          LOGGER.error("Unknown connector type \"{}\"", type);
          break;
      }
    }
    return klazz;
  }
}