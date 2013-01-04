package com.wealdtech.jersey.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Filter to log requests and how long it takes for them to be processed. Also
 * creates a request ID that is returned to the requestor and can be used to
 * track the request.
 */
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

  private static final String REQUESTHIDEADER = "Request-ID";
  private static final String REQUESTID = "REQUESTID";
  private static final String REQUESTSTARTTIME = "RSTARTTIME";

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final String requestid = Long.toHexString(Double.doubleToLongBits(Math.random()));
    MDC.put(REQUESTID, requestid);
    MDC.put(REQUESTSTARTTIME, String.valueOf(System.currentTimeMillis()));
    if (LOGGER.isInfoEnabled())
    {
      LOGGER.info("Started: {} {} ({})", request.getMethod(), request.getPath(), requestid);
    }

    return request;
  }

  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
  {
    final Object object = MDC.get(REQUESTSTARTTIME);
    if (object instanceof Long)
    {
      final Long startTime = (Long)object;
      final String rid = MDC.get(REQUESTID);
      final long duration = System.currentTimeMillis() - startTime;
      response.getHttpHeaders().add(REQUESTHIDEADER, rid);
      LOGGER.info("Finished: {} {} ({} ms)", request.getMethod(), request.getPath(), String.valueOf(duration));
    }
    else
    {
      LOGGER.warn("Finished {} {}", request.getMethod(), request.getPath());
    }
    return response;
  }
}
