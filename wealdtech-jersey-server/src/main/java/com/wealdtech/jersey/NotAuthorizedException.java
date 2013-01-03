package com.wealdtech.jersey;

import javax.ws.rs.core.Response.Status;

/**
 * Exception for requests which require but do not have authorization.
 */
public class NotAuthorizedException extends HttpException
{
  private static final long serialVersionUID = 1238638821988189695L;

  public NotAuthorizedException(final String msg, final Throwable t)
  {
    super(new DaemonStatus(Status.UNAUTHORIZED, flattenMessage(msg)), null, t);
  }

  public NotAuthorizedException(final String msg)
  {
    this(msg, null);
  }
}
