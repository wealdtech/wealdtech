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
  private String host = "localhost";
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
  private JettyServerConfiguration(@JsonProperty("host") final String host,
                                   @JsonProperty("threadpool") final ThreadPoolConfiguration threadPoolConfiguration,
                                   @JsonProperty("ssl") final SslConfiguration sslConfiguration,
                                   @JsonProperty("connectors") final List<ConnectorConfiguration> connectorConfigurations,
                                   @JsonProperty("response") final JettyResponseConfiguration responseConfiguration,
                                   @JsonProperty("jersey") final JerseyServerConfiguration jerseyConfiguration)
  {
    this.host = Objects.firstNonNull(host, this.host);
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

    public String getKeystorePath()
    {
      return this.keystorepath;
    }

    public String getKeystorePassword()
    {
      return this.keystorepasswd;
    }

    public String getManagerPassword()
    {
      return this.keymanagerpasswd;
    }
  }

  public static class ConnectorConfiguration implements Configuration
  {
    private int acceptors = 4;

    private int acceptqueuesize = -1;

    private boolean usedirectbuffers = true;

    private int lowresourcesconnections = 0;

    private int port = 8080;

    private boolean secure = false;

    public ConnectorConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private ConnectorConfiguration(@JsonProperty("acceptors") final Integer acceptors,
                                   @JsonProperty("acceptqueuesize") final Integer acceptqueuesize,
                                   @JsonProperty("usedirectbuffers") final Boolean usedirectbuffers,
                                   @JsonProperty("lowresourcesconnections") final Integer lowresourcesconnections,
                                   @JsonProperty("port") final Integer port,
                                   @JsonProperty("secure") final Boolean secure,
                                   @JsonProperty("keystorepath") final String keystorepath,
                                   @JsonProperty("keystorepasswd") final String keystorepasswd,
                                   @JsonProperty("keymanagerpasswd") final String keymanagerpasswd)
    {
      this.acceptors = Objects.firstNonNull(acceptors, this.acceptors);
      this.acceptqueuesize = Objects.firstNonNull(acceptqueuesize, this.acceptqueuesize);
      this.usedirectbuffers = Objects.firstNonNull(usedirectbuffers, this.usedirectbuffers);
      this.lowresourcesconnections = Objects.firstNonNull(lowresourcesconnections, this.lowresourcesconnections);
      this.port = Objects.firstNonNull(port, this.port);
      this.secure = Objects.firstNonNull(secure, this.secure);
    }

    public int getAcceptors()
    {
      return this.acceptors;
    }

    public int getAcceptQueueSize()
    {
      return this.acceptqueuesize;
    }

    public boolean getUseDirectBuffers()
    {
      return this.usedirectbuffers;
    }

    public int getLowResourcesConnections()
    {
      return this.lowresourcesconnections;
    }

    public int getPort()
    {
      return this.port;
    }

    public boolean isSecure()
    {
      return this.secure;
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
