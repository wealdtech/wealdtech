package com.wealdtech.utils;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

/**
 * Setup for gathering and reporting metrics for Weald Technology
 * utilities, using {@link MetricRegistry}
 */
public enum WealdMetrics
{
  INSTANCE;

  private static final MetricRegistry registry;

  static
  {
    registry = new MetricRegistry();
    final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
    reporter.start();
  }

  public static MetricRegistry defaultRegistry()
  {
    return registry;
  }
}
