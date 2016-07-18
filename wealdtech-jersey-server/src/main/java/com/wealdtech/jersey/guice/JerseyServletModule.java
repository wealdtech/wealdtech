/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JerseyServletModule extends ServletModule
{
  private Collection<String> packages;
  private Collection<Class<? extends ResourceFilterFactory>> resourceFilterFactories;
  private Collection<Class<? extends ContainerRequestFilter>> requestFilters;
  private Collection<Class<? extends ContainerResponseFilter>> responseFilters;

  static
  {
    // Get rid of j.u.l. and install SLF4J - comment out for now whilst we work out if we need this or not
//    LogManager.getLogManager().reset();
//    SLF4JBridgeHandler.install();
//    Logger.getLogger("global").setLevel(Level.FINEST);
  }

  /**
   * Create a Jersey servlet module.
   *
   * @param resourceFilterFactories a set of class names for resource filter factories
   * @param requestFilters a set of class names for request filters
   * @param responseFilters a set of class names for response filters
   * @param packages a collection of names of packages in which to look for resources
   */
  public JerseyServletModule(final Collection<Class<? extends ResourceFilterFactory>> resourceFilterFactories,
                             final Collection<Class<? extends ContainerRequestFilter>> requestFilters,
                             final Collection<Class<? extends ContainerResponseFilter>> responseFilters,
                             final Collection<String> packages)
  {
    super();
    this.resourceFilterFactories = resourceFilterFactories;
    this.requestFilters = requestFilters;
    this.responseFilters = responseFilters;
    this.packages = packages;
  }

  /**
   * Create a Jersey servlet module.
   *
   * @param requestFilters a set of class names for request filters
   * @param responseFilters a set of class names for response filters
   * @param packages a collection of names of packages in which to look for resources
   */
  public JerseyServletModule(final Collection<Class<? extends ContainerRequestFilter>> requestFilters,
                             final Collection<Class<? extends ContainerResponseFilter>> responseFilters,
                             final Collection<String> packages)
  {
    super();
    this.resourceFilterFactories = null;
    this.requestFilters = requestFilters;
    this.responseFilters = responseFilters;
    this.packages = packages;
  }

  @Override
  protected void configureServlets()
  {
    final Map<String, String> params = new HashMap<String, String>();
    params.put(PackagesResourceConfig.PROPERTY_PACKAGES,
               Joiner.on(",").join(Iterables.concat(ImmutableList.of("com.wealdtech.jersey"), packages)));
    if (resourceFilterFactories != null)
    {
      params.put(PackagesResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, Joiner.on(",").join(resourceFilterFactories));
    }
    params.put(PackagesResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, Joiner.on(",").join(requestFilters));
    params.put(PackagesResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, Joiner.on(",").join(responseFilters));

    params.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");

    serve("/*").with(GuiceContainer.class, params);
  }

  /**
   * Convert a collection of classes in to a comma-separated string of class names
   */
  private String joinClassNames(final Collection<Class<?>> klazzes)
  {
    final Collection<String> names = Collections2.transform(Sets.newHashSet(klazzes), new Function<Class<?>, String>()
    {
      @Override
      public String apply(Class<?> klazz)
      {
        return klazz.getName();
      }
    });

    return Joiner.on(',').skipNulls().join(names);
  }

  @Provides
  @Singleton
  JacksonJsonProvider jacksonJsonProvider(final ObjectMapper mapper)
  {
    return new JacksonJsonProvider(mapper);
  }
}
