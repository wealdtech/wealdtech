package com.wealdtech.utils;

import com.codahale.metrics.MetricRegistry;

/**
 * Container for a single static {@link MetricRegistry}
 */
public enum WealdMetrics
{
  INSTANCE;

  private static final MetricRegistry registry;

  static
  {
    registry = new MetricRegistry();
  }

  public static MetricRegistry defaultRegistry()
  {
    return registry;
  }
}
