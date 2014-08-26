/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A servlet filter which adds the request method and URI to the thread name
 * processing the request for the duration of the request.
 */
public class ThreadNameFilter implements Filter
{
  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
  }

  @Override
  public void destroy()
  {
  }

  @Override
  public void doFilter(final ServletRequest servletRequest,
                       final ServletResponse servletResponse,
                       final FilterChain chain) throws IOException, ServletException
  {
    final HttpServletRequest req = (HttpServletRequest)servletRequest;
    final Thread current = Thread.currentThread();
    final String oldName = current.getName();
    try
    {
      current.setName(addRequestDetails(req, oldName));
      chain.doFilter(servletRequest, servletResponse);
    }
    finally
    {
      current.setName(oldName);
    }
  }

  private static String addRequestDetails(final HttpServletRequest servletRequest, final String baseName)
  {
    return baseName + " - " + servletRequest.getMethod() + ' ' + servletRequest.getPathInfo();
  }
}
