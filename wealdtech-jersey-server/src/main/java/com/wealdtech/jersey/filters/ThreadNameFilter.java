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
  { /* unused */
  }

  @Override
  public void destroy()
  { /* unused */
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
