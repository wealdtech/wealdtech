/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.config;

import com.palominolabs.metrics.guice.MetricsInstrumentationModule;
import com.wealdtech.utils.WealdMetrics;

/**
 * Weald Technology's version of the Instrumentation module, to ensure that we supply the Weald Technology metric registry and
 * ensure that we retain a single registry
 */
public class WealdInstrumentationModule extends MetricsInstrumentationModule
{
  public WealdInstrumentationModule()
  {
    super(WealdMetrics.getMetricRegistry());

  }
}
