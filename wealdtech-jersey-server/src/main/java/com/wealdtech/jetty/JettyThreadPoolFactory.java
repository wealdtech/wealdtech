package com.wealdtech.jetty;

import org.eclipse.jetty.util.thread.ThreadPool;

import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;
import com.wealdtech.jetty.config.JettyThreadPoolConfiguration;
import com.wealdtech.utils.WealdMetrics;

public class JettyThreadPoolFactory
{
  public static ThreadPool build(final JettyThreadPoolConfiguration configuration)
  {
    final InstrumentedQueuedThreadPool pool = new InstrumentedQueuedThreadPool(WealdMetrics.defaultRegistry());

    pool.setMinThreads(configuration.getMinThreads());
    pool.setMaxThreads(configuration.getMaxThreads());
    pool.setIdleTimeout(configuration.getMaxIdleTime());
    return pool;
  }
}
