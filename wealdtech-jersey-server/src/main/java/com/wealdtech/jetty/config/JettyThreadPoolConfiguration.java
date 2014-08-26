/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jetty.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.wealdtech.configuration.Configuration;

/**
 * Configuration for a Jetty thread pool.
 * <p>
 * The thread pool contains bounds for the number of threads as well as how long
 * a thread can remain idle before being retired
 */
public final class JettyThreadPoolConfiguration implements Configuration
{
  private int minThreads = 8;
  private int maxThreads = 256;
  private int idleTimeout = 120000;

  public JettyThreadPoolConfiguration()
  {
    // 0-configuration defaults
  }

  @JsonCreator
  private JettyThreadPoolConfiguration(@JsonProperty("minthreads") final Integer minThreads,
                                       @JsonProperty("maxthreads") final Integer maxThreads,
                                       @JsonProperty("idletimeout") final Integer idleTimeout)
  {
    this.minThreads = Objects.firstNonNull(minThreads, this.minThreads);
    this.maxThreads = Objects.firstNonNull(maxThreads, this.maxThreads);
    this.idleTimeout = Objects.firstNonNull(idleTimeout, this.idleTimeout);
  }

  public int getMinThreads()
  {
    return this.minThreads;
  }

  public int getMaxThreads()
  {
    return this.maxThreads;
  }

  public int getIdleTimeout()
  {
    return this.idleTimeout;
  }
}
