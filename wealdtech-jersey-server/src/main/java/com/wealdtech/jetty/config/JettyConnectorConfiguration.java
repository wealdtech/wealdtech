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

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.jetty.JettyConnectorFactory;
import com.wealdtech.jetty.JettyHttpConnectorFactory;
import com.wealdtech.jetty.JettyHttpsConnectorFactory;

/**
 * Configuration for a Jetty connector.
 */
public final class JettyConnectorConfiguration implements Configuration
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettyConnectorConfiguration.class);

  private String bindHost = "localhost";
  private int port = 8080;
  private Class<? extends JettyConnectorFactory> type = JettyHttpConnectorFactory.class;
  private int acceptQueueSize = -1;
  private long idleTimeout = 50000;
  private boolean reuseAddress = false;
  private int soLingerTime = 60;
  private int inputBufferSize = 32768;
  private int outputBufferSize = 32768;
  private int requestHeaderSize = 8192;
  private int responseHeaderSize = 8192;
  private int headerCacheSize = 512;
  private boolean sendServerVersion = true;
  private boolean sendDateHeader = true;
  private boolean useForwardedHeaders = true;
  private int acceptorThreads = 8;
  private int selectorThreads = 8;
  private int minBufferPoolSize = 64;
  private int bufferPoolIncrement = 2048;
  private int maxBufferPoolSize = 65536;

  public JettyConnectorConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettyConnectorConfiguration(@JsonProperty("bindhost") final String bindHost,
                                      @JsonProperty("port") final Integer port,
                                      @JsonProperty("type") final String type,
                                      @JsonProperty("acceptqueuesize") final Integer acceptQueueSize,
                                      @JsonProperty("idletimeout") final Long idleTimeout,
                                      @JsonProperty("reuseaddress") final Boolean reuseAddress,
                                      @JsonProperty("solingertime") final Integer soLingerTime,
                                      @JsonProperty("headercachesize") final Integer headerCacheSize,
                                      @JsonProperty("inputbuffersize") final Integer inputBufferSize,
                                      @JsonProperty("outputbuffersize") final Integer outputBufferSize,
                                      @JsonProperty("requestheadersize") final Integer requestHeaderSize,
                                      @JsonProperty("responseheadersize") final Integer responseHeaderSize,
                                      @JsonProperty("sendserverversion") final Boolean sendServerVersion,
                                      @JsonProperty("senddateheader") final Boolean sendDateHeader,
                                      @JsonProperty("useforwardedheaders") final Boolean userForwardedHeaders,
                                      @JsonProperty("acceptorthreads") final Integer acceptorThreads,
                                      @JsonProperty("selectorthreads") final Integer selectorThreads,
                                      @JsonProperty("minbufferpoolsize") final Integer minBufferPoolSize,
                                      @JsonProperty("bufferpoolincrement") final Integer bufferPoolIncremenet,
                                      @JsonProperty("maxbufferpoolsize") final Integer maxBufferPoolSize)
  {
    this.bindHost = MoreObjects.firstNonNull(bindHost, this.bindHost);
    this.port = MoreObjects.firstNonNull(port, this.port);
    this.type = MoreObjects.firstNonNull(classFromType(type), this.type);
    this.acceptQueueSize = MoreObjects.firstNonNull(acceptQueueSize, this.acceptQueueSize);
    this.idleTimeout = MoreObjects.firstNonNull(idleTimeout, this.idleTimeout);
    this.reuseAddress = MoreObjects.firstNonNull(reuseAddress, this.reuseAddress);
    this.soLingerTime = MoreObjects.firstNonNull(soLingerTime, this.soLingerTime);
    this.requestHeaderSize = MoreObjects.firstNonNull(requestHeaderSize, this.requestHeaderSize);
    this.responseHeaderSize = MoreObjects.firstNonNull(responseHeaderSize, this.responseHeaderSize);
    this.headerCacheSize = MoreObjects.firstNonNull(inputBufferSize, this.headerCacheSize);
    this.inputBufferSize = MoreObjects.firstNonNull(inputBufferSize, this.inputBufferSize);
    this.outputBufferSize = MoreObjects.firstNonNull(outputBufferSize, this.outputBufferSize);
    this.sendServerVersion = MoreObjects.firstNonNull(sendServerVersion, this.sendServerVersion);
    this.sendDateHeader = MoreObjects.firstNonNull(sendDateHeader, this.sendDateHeader);
    this.useForwardedHeaders = MoreObjects.firstNonNull(useForwardedHeaders, this.useForwardedHeaders);
    this.acceptorThreads = MoreObjects.firstNonNull(acceptorThreads, this.acceptorThreads);
    this.selectorThreads = MoreObjects.firstNonNull(selectorThreads, this.selectorThreads);
    this.minBufferPoolSize = MoreObjects.firstNonNull(minBufferPoolSize, this.minBufferPoolSize);
    this.bufferPoolIncrement = MoreObjects.firstNonNull(bufferPoolIncrement, this.bufferPoolIncrement);
    this.maxBufferPoolSize = MoreObjects.firstNonNull(maxBufferPoolSize, this.maxBufferPoolSize);
  }

  public String getBindHost()
  {
    return this.bindHost;
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
    return this.acceptQueueSize;
  }

  public long getIdleTimeout()
  {
    return this.idleTimeout;
  }

  public boolean getReuseAddress()
  {
    return this.reuseAddress;
  }

  public int getSoLingerTime()
  {
    return this.soLingerTime;
  }

  public int getRequestHeaderSize()
  {
    return this.requestHeaderSize;
  }

  public int getResponseHeaderSize()
  {
    return this.responseHeaderSize;
  }

  public int getHeaderCacheSize()
  {
    return this.headerCacheSize;
  }

  public int getInputBufferSize()
  {
    return this.inputBufferSize;
  }

  public int getOutputBufferSize()
  {
    return this.outputBufferSize;
  }

  public boolean getSendServerVersion()
  {
    return this.sendServerVersion;
  }

  public boolean getSendDateHeader()
  {
    return this.sendDateHeader;
  }

  public boolean useForwardedHeaders()
  {
    return this.useForwardedHeaders;
  }

  public int getAcceptorThreads()
  {
    return this.acceptorThreads;
  }

  public int getSelectorThreads()
  {
    return this.selectorThreads;
  }

  public int getMinBufferPoolSize()
  {
    return this.minBufferPoolSize;
  }

  public int getBufferPoolIncrement()
  {
    return this.bufferPoolIncrement;
  }

  public int getMaxBufferPoolSize()
  {
    return this.maxBufferPoolSize;
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
