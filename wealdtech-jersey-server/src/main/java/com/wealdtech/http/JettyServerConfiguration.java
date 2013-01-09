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
import com.wealdtech.jersey.JerseyServerConfiguration;

/**
 * Configuration for a Jetty server.
 */
public class JettyServerConfiguration
{
  private String host = "localhost";

  private int port = 8080;

  private JettyResponseConfiguration response = new JettyResponseConfiguration();

  private JerseyServerConfiguration jersey = new JerseyServerConfiguration();

  private ConnectorConfiguration connector = new ConnectorConfiguration();

  private ThreadPoolConfiguration threadpool = new ThreadPoolConfiguration();

  @JsonCreator
  private JettyServerConfiguration(@JsonProperty("host") final String host,
                                   @JsonProperty("port") final Integer port,
                                   @JsonProperty("response") final JettyResponseConfiguration response,
                                   @JsonProperty("jersey") final JerseyServerConfiguration jersey,
                                   @JsonProperty("connector") final ConnectorConfiguration connector,
                                   @JsonProperty("threadpool") final ThreadPoolConfiguration threadpool)
  {
    if (host != null)
    {
      this.host = host;
    }
    if (port != null)
    {
      this.port = port;
    }
    if (response != null)
    {
      this.response = response;
    }
    if (jersey != null)
    {
      this.jersey = jersey;
    }
    if (connector != null)
    {
      this.connector = connector;
    }
    if (threadpool != null)
    {
      this.threadpool = threadpool;
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
    return this.response;
  }

  public JerseyServerConfiguration getJerseyConfiguration()
  {
    return this.jersey;
  }

  public ConnectorConfiguration getConnector()
  {
    return this.connector;
  }

  public ThreadPoolConfiguration getThreadPool()
  {
    return this.threadpool;
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
