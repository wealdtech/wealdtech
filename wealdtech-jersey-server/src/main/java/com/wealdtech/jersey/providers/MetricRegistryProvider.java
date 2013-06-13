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

package com.wealdtech.jersey.providers;

import javax.ws.rs.ext.Provider;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.core.HttpContext;
import com.wealdtech.utils.WealdMetrics;

/**
 * Provide a Metric registry.
 */
@Provider
public class MetricRegistryProvider extends AbstractInjectableProvider<MetricRegistry>
{
  public MetricRegistryProvider()
  {
    super(MetricRegistry.class);
  }

  /**
   * Provide object mapper.
   */
  @Override
  public MetricRegistry getValue(final HttpContext c)
  {
    return WealdMetrics.defaultRegistry();
  }
}
