package com.wealdtech.jersey;

import javax.ws.rs.WebApplicationException;

/**
 * Base class for daemon HTTP exceptions.
 */
public class HttpException extends WebApplicationException
{
  private static final long serialVersionUID = 300455569925578661L;

  private transient final DaemonStatus status;
  private transient final String message;
  private transient final int retryAfter;

  public HttpException(final DaemonStatus status, final String message, final int retryAfter, final Throwable t)
  {
    super(t);
    this.status = status;
    this.message = message;
    this.retryAfter = retryAfter;
  }

  public HttpException(final DaemonStatus status, final String message, final Throwable t)
  {
    this(status, message, -1, t);
  }

  public DaemonStatus getStatus()
  {
    return status;
  }

  @Override
  public String getMessage()
  {
    return message;
  }

  public int getRetryAfter()
  {
    return retryAfter;
  }

  protected static String flattenMessage(final String msg)
  {
    return msg.replaceAll("\n", " ");
  }
}
