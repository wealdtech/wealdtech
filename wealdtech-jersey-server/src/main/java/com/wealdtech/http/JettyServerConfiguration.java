package com.wealdtech.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JettyServerConfiguration
{
  private String host = "localhost";

  private int port = 8080;

  private ConnectorConfiguration connector = new ConnectorConfiguration();

  private ThreadPoolConfiguration threadpool = new ThreadPoolConfiguration();

  private JettyServerConfiguration(@JsonProperty("host") final String host,
                                   @JsonProperty("port") final Integer port,
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

  public ConnectorConfiguration getConnector()
  {
    return this.connector;
  }

  public ThreadPoolConfiguration getThreadPool()
  {
    return this.threadpool;
  }

  public static class ConnectorConfiguration
  {
    private int acceptors = 4;

    private int acceptqueuesize = -1;

    private boolean usedirectbuffers = true;

    private int lowresourcesconnections = 0;

    public ConnectorConfiguration()
    {
      // Just use defaults;
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
