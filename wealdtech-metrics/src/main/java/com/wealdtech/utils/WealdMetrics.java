package com.wealdtech.utils;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Setup for gathering and reporting metrics for Weald Technology
 * utilities, using {@link MetricRegistry}
 */
public enum WealdMetrics
{
  INSTANCE;

  private static final MetricRegistry metricRegistry;
  private static final HealthCheckRegistry healthCheckRegistry;

  static
  {
    metricRegistry = new MetricRegistry();
    healthCheckRegistry = new HealthCheckRegistry();
    final JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
    reporter.start();
  }

  public static MetricRegistry getMetricRegistry()
  {
    return metricRegistry;
  }

  public static HealthCheckRegistry getHealthCheckRegistry()
  {
    return healthCheckRegistry;
  }
}
