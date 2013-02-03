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

package com.wealdtech.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.wealdtech.jersey.JerseyServerConfiguration;

/**
 * Configuration for a Jetty server.
 */
public class JettyServerConfiguration
{
  private String host = "localhost";
  private int port = 8080;
  private JettyResponseConfiguration responseConfiguration = new JettyResponseConfiguration();
  private JerseyServerConfiguration jerseyConfiguration = new JerseyServerConfiguration();
  private ConnectorConfiguration connectorConfiguration = new ConnectorConfiguration();
  private ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();

  @Inject
  private JettyServerConfiguration()
  {

  }

  @JsonCreator
  private JettyServerConfiguration(@JsonProperty("host") final String host,
                                   @JsonProperty("port") final Integer port,
                                   @JsonProperty("response") final JettyResponseConfiguration responseConfiguration,
                                   @JsonProperty("jersey") final JerseyServerConfiguration jerseyConfiguration,
                                   @JsonProperty("connector") final ConnectorConfiguration connectorConfiguration,
                                   @JsonProperty("threadpool") final ThreadPoolConfiguration threadPoolConfiguration)
  {
    if (host != null)
    {
      this.host = host;
    }
    if (port != null)
    {
      this.port = port;
    }
    if (responseConfiguration != null)
    {
      this.responseConfiguration = responseConfiguration;
    }
    if (jerseyConfiguration != null)
    {
      this.jerseyConfiguration = jerseyConfiguration;
    }
    if (connectorConfiguration != null)
    {
      this.connectorConfiguration = connectorConfiguration;
    }
    if (threadPoolConfiguration != null)
    {
      this.threadPoolConfiguration = threadPoolConfiguration;
    }
  }

  public String getHost()
  {
    return this.host;
  }

  public int getPort()
  {
    return this.port;
  }

  public JettyResponseConfiguration getResponseConfiguration()
  {
    return this.responseConfiguration;
  }

  public JerseyServerConfiguration getJerseyConfiguration()
  {
    return this.jerseyConfiguration;
  }

  public ConnectorConfiguration getConnectorConfiguration()
  {
    return this.connectorConfiguration;
  }

  public ThreadPoolConfiguration getThreadPoolConfiguration()
  {
    return this.threadPoolConfiguration;
  }

  public static class Builder
  {
    private String host;
    private Integer port;
    private JettyResponseConfiguration responseConfiguration;
    private JerseyServerConfiguration jerseyConfiguration;
    private ConnectorConfiguration connectorConfiguration;
    private ThreadPoolConfiguration threadPoolConfiguration;

    /**
     * Start to build a Jetty server configuration.
     */
    public Builder()
    {
    }

    /**
     * Start to build a Jetty server configuration based on a prior configuration.
     * @param prior the prior configuration.
     */
    public Builder(final JettyServerConfiguration prior)
    {
      this.host = prior.host;
      this.port = prior.port;
      this.responseConfiguration = prior.responseConfiguration;
      this.jerseyConfiguration = prior.jerseyConfiguration;
      this.connectorConfiguration = prior.connectorConfiguration;
      this.threadPoolConfiguration = prior.threadPoolConfiguration;
    }

    public Builder host(final String host)
    {
      this.host = host;
      return this;
    }

    public Builder port(final Integer port)
    {
      this.port = port;
      return this;
    }

    public Builder responseConfiguration(final JettyResponseConfiguration responseConfiguration)
    {
      this.responseConfiguration = responseConfiguration;
      return this;
    }

    public Builder jerseyConfiguration(final JerseyServerConfiguration jerseyConfiguration)
    {
      this.jerseyConfiguration = jerseyConfiguration;
      return this;
    }

    public Builder connectorConfiguration(final ConnectorConfiguration connectorConfiguration)
    {
      this.connectorConfiguration = connectorConfiguration;
      return this;
    }

    public Builder threadPoolConfiguration(final ThreadPoolConfiguration threadPoolConfiguration)
    {
      this.threadPoolConfiguration = threadPoolConfiguration;
      return this;
    }

    public JettyServerConfiguration build()
    {
      return new JettyServerConfiguration(this.host, this.port, this.responseConfiguration, this.jerseyConfiguration, this.connectorConfiguration, this.threadPoolConfiguration);
    }
  }

  public static class JettyResponseConfiguration
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
      if (serverName != null)
      {
        this.serverName = serverName;
      }
      if (retryPeriod != null)
      {
        this.retryPeriod = retryPeriod;
      }
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

  public static class ConnectorConfiguration
  {
    private int acceptors = 4;

    private int acceptqueuesize = -1;

    private boolean usedirectbuffers = true;

    private int lowresourcesconnections = 0;

    public ConnectorConfiguration()
    {
      // Just use defaults
    }

    @JsonCreator
    private ConnectorConfiguration(@JsonProperty("acceptors") final Integer acceptors,
                                   @JsonProperty("acceptqueuesize") final Integer acceptqueuesize,
                                   @JsonProperty("usedirectbuffers") final Boolean usedirectbuffers,
                                   @JsonProperty("lowresourcesconnections") final Integer lowresourcesconnections)
    {
      if (acceptors != null)
      {
        this.acceptors = acceptors;
      }
      if (acceptqueuesize != null)
      {
        this.acceptqueuesize = acceptqueuesize;
      }
      if (usedirectbuffers != null)
      {
        this.usedirectbuffers = usedirectbuffers;
      }
      if (lowresourcesconnections != null)
      {
        this.lowresourcesconnections = lowresourcesconnections;
      }
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
      if (minthreads != null)
      {
        this.minthreads = minthreads;
      }
      if (maxidletimems != null)
      {
        this.maxidletimems = maxidletimems;
      }
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
