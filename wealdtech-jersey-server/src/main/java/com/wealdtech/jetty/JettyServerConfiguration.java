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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.jersey.JerseyServerConfiguration;

/**
 * Configuration for a Jetty server.
 */
public final class JettyServerConfiguration implements Configuration
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JettyServerConfiguration.class);

  private final String host = "localhost";
  private final SslConfiguration sslConfiguration = new SslConfiguration();
  private ImmutableList<ConnectorConfiguration> connectorConfigurations = ImmutableList.of(new ConnectorConfiguration());
  private JettyResponseConfiguration responseConfiguration = new JettyResponseConfiguration();
  private JerseyServerConfiguration jerseyConfiguration = new JerseyServerConfiguration();
  private ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();

  @Inject
  public JettyServerConfiguration()
  {
    // 0-configuration injection
  }

  @JsonCreator
  private JettyServerConfiguration(@JsonProperty("name") final String name,
                                   @JsonProperty("host") final String host,
                                   @JsonProperty("threadpool") final ThreadPoolConfiguration threadPoolConfiguration,
                                   @JsonProperty("ssl") final SslConfiguration sslConfiguration,
                                   @JsonProperty("connectors") final List<ConnectorConfiguration> connectorConfigurations,
                                   @JsonProperty("response") final JettyResponseConfiguration responseConfiguration,
                                   @JsonProperty("jersey") final JerseyServerConfiguration jerseyConfiguration)
  {
    this.threadPoolConfiguration = Objects.firstNonNull(threadPoolConfiguration, this.threadPoolConfiguration);
    this.responseConfiguration = Objects.firstNonNull(responseConfiguration, this.responseConfiguration);
    this.jerseyConfiguration = Objects.firstNonNull(jerseyConfiguration, this.jerseyConfiguration);
    this.connectorConfigurations = ImmutableList.copyOf(Objects.firstNonNull(connectorConfigurations, this.connectorConfigurations));
  }

  public String getHost()
  {
    return this.host;
  }

  public ThreadPoolConfiguration getThreadPoolConfiguration()
  {
    return this.threadPoolConfiguration;
  }

  public SslConfiguration getSslConfiguration()
  {
    return this.sslConfiguration;
  }

  public ImmutableList<ConnectorConfiguration> getConnectorConfigurations()
  {
    return this.connectorConfigurations;
  }

  public JettyResponseConfiguration getResponseConfiguration()
  {
    return this.responseConfiguration;
  }

  public JerseyServerConfiguration getJerseyConfiguration()
  {
    return this.jerseyConfiguration;
  }

  public static class JettyResponseConfiguration implements Configuration
  {
    private String serverName = "Weald Technology server";

    private int retryPeriod = 60;

    public JettyResponseConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private JettyResponseConfiguration(@JsonProperty("servername") final String serverName,
                                       @JsonProperty("retryperiod") final Integer retryPeriod)
    {
      this.serverName = Objects.firstNonNull(serverName, this.serverName);
      this.retryPeriod = Objects.firstNonNull(retryPeriod, this.retryPeriod);
    }

    public String getServerName()
    {
      return this.serverName;
    }

    public int getRetryPeriod()
    {
      return this.retryPeriod;
    }
  }

  public static class SslConfiguration implements Configuration
  {
    private String keystorepath = "/etc/keystore";

    private String keystorepasswd = "test";

    private String keymanagerpasswd = "test";

    public SslConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private SslConfiguration(@JsonProperty("keystorepath") final String keystorepath,
                             @JsonProperty("keystorepassword") final String keystorepasswd,
                             @JsonProperty("keymanagerpassword") final String keymanagerpasswd)
    {
      this.keystorepath = Objects.firstNonNull(keystorepath, this.keystorepath);
      this.keystorepasswd = Objects.firstNonNull(keystorepasswd, this.keystorepasswd);
      this.keymanagerpasswd = Objects.firstNonNull(keymanagerpasswd, this.keymanagerpasswd);
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

  public static class ConnectorConfiguration implements Configuration
  {
    private String name = "Server";
    private String host = "localhost";
    private int port = 8080;
    private Class<? extends JettyConnector> type = JettyHttpConnector.class;
    private int acceptqueuesize = -1;
    private long idletimeout = 50000;
    private boolean reuseaddress = false;
    private int solingertime = 60;
    private int inputbuffersize = 2048;

    public ConnectorConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private ConnectorConfiguration(@JsonProperty("name") final String name,
                                   @JsonProperty("host") final String host,
                                   @JsonProperty("port") final Integer port,
                                   @JsonProperty("type") final String type,
                                   @JsonProperty("acceptqueuesize") final Integer acceptqueuesize,
                                   @JsonProperty("idletimeout") final Long idletimeout,
                                   @JsonProperty("reuseaddress") final Boolean reuseaddress,
                                   @JsonProperty("solingertime") final Integer solingertime,
                                   @JsonProperty("inputbuffersize") final Integer inputbuffersize)
    {
      this.name = Objects.firstNonNull(name, this.name);
      this.host = Objects.firstNonNull(host, this.host);
      this.port = Objects.firstNonNull(port, this.port);
      this.type = Objects.firstNonNull(classFromType(type), this.type);
      this.acceptqueuesize = Objects.firstNonNull(acceptqueuesize, this.acceptqueuesize);
      this.idletimeout = Objects.firstNonNull(idletimeout, this.idletimeout);
      this.reuseaddress = Objects.firstNonNull(reuseaddress, this.reuseaddress);
      this.solingertime = Objects.firstNonNull(solingertime, this.solingertime);
      this.inputbuffersize = Objects.firstNonNull(inputbuffersize, this.inputbuffersize);
    }

    /**
     * Obtain a Jetty connector given a type name
     */
    private Class<? extends JettyConnector> classFromType(final String type)
    {
      Class<? extends JettyConnector> klazz = null;
      if (type != null)
      {
        switch (type.toLowerCase())
        {
          case "http":
            klazz = JettyHttpConnector.class;
            break;
          case "https":
            klazz =  JettyHttpsConnector.class;
            break;
          default:
            LOGGER.error("Unknown connector type \"{}\"", type);
            break;
        }
      }
      return klazz;
    }

    public String getName()
    {
      return this.name;
    }

    public String getHost()
    {
      return this.host;
    }

    public int getPort()
    {
      return this.port;
    }

    public Class<? extends JettyConnector> getType()
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
  }

  public static class ThreadPoolConfiguration
  {
    private int minthreads = 8;

    private int maxidletimems = 120000;

    public ThreadPoolConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private ThreadPoolConfiguration(@JsonProperty("minthreads") final Integer minthreads,
                                    @JsonProperty("maxidletimems") final Integer maxidletimems)
    {
      this.minthreads = Objects.firstNonNull(minthreads, this.minthreads);
      this.maxidletimems = Objects.firstNonNull(maxidletimems, this.maxidletimems);
    }

    public int getMinThreads()
    {
      return this.minthreads;
    }

    public int getMaxIdleTimeMs()
    {
      return this.maxidletimems;
    }
  }

}
