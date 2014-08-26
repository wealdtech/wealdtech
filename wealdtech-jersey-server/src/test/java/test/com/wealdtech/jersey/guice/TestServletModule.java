/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jersey.guice;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * A Guice module to configure a Jetty server with servlets
 */
public class TestServletModule extends ServletModule
{
  private String packages;

  /**
   * Create a Jersey servlet module with a list of packages to
   * check for resources and the like.
   * @param packages a list of packages
   */
  public TestServletModule(final String... packages)
  {
    super();
    setPackages(packages);
  }

  @Override
  protected void configureServlets()
  {
    final Map<String, String> params = new HashMap<String, String>();
    params.put(PackagesResourceConfig.PROPERTY_PACKAGES, this.packages);

    serve("/*").with(GuiceContainer.class, params);
  }

  /**
   * Set the list of packages that we will search for providers and the like
   * @param additionalPackages The packages above and beyond our standard list
   */
  private void setPackages(final String... additionalPackages)
  {
    String[] packagesList = ObjectArrays.concat("com.wealdtech.jersey", additionalPackages);
    packagesList = ObjectArrays.concat("com.yammer.metrics", packagesList);
    this.packages = Joiner.on(',').skipNulls().join(packagesList);
  }
}
