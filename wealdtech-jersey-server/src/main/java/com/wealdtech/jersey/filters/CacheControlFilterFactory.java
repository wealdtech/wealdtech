/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.*;
import com.wealdtech.jersey.annotations.CacheMaxAge;
import com.wealdtech.jersey.annotations.NoCache;

import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.List;

public class CacheControlFilterFactory implements ResourceFilterFactory
{
  @Override
  public List<ResourceFilter> create(AbstractMethod am)
  {
    if (am.isAnnotationPresent(CacheMaxAge.class))
    {
      CacheMaxAge maxAge = am.getAnnotation(CacheMaxAge.class);
      return newCacheFilter("max-age=" + maxAge.unit().toSeconds(maxAge.time()));
    }
    else if (am.isAnnotationPresent(NoCache.class))
    {
      return newCacheFilter("no-cache");
    }
    else
    {
      return Collections.emptyList();
    }
  }

  private List<ResourceFilter> newCacheFilter(String content)
  {
    return Collections.<ResourceFilter>singletonList(new CacheResponseFilter(content));
  }

  private static class CacheResponseFilter implements ResourceFilter, ContainerResponseFilter
  {
    private final String headerValue;

    CacheResponseFilter(String headerValue)
    {
      this.headerValue = headerValue;
    }

    @Override
    public ContainerRequestFilter getRequestFilter()
    {
      return null;
    }

    @Override
    public ContainerResponseFilter getResponseFilter()
    {
      return this;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
    {
      response.getHttpHeaders().putSingle(HttpHeaders.CACHE_CONTROL, headerValue);
      return response;
    }
  }
}