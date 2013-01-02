package com.wealdtech.jersey;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;

/**
 * Status with overrideable reason phrase.
 * Implemented as a class because the default Status
 * is an enum
 */
public class DaemonStatus implements StatusType
{
  private transient final StatusType status;
  private transient String reason;

  public DaemonStatus(final int statuscode)
  {
    this(statuscode, null);
  }

  public DaemonStatus(final int statuscode, final String reasonphrase)
  {
    this.status = Status.fromStatusCode(statuscode);
    this.reason = reasonphrase;
  }

  public DaemonStatus(final StatusType status)
  {
    this(status, null);
  }

  public DaemonStatus(final StatusType status, final String reasonphrase)
  {
    this.status = status;
    this.reason = reasonphrase;
  }

  /**
   * Get the class of status code
   * 
   * @return the class of status code
   */
  public Family getFamily()
  {
    return this.status.getFamily();
  }

  /**
   * Get the associated status code
   * 
   * @return the status code
   */
  public int getStatusCode()
  {
    return this.status.getStatusCode();
  }

  /**
   * Get the reason phrase
   * 
   * @return the reason phrase
   */
  public String getReasonPhrase()
  {
    return toString();
  }

  /**
   * Set the reason phrase
   * 
   * @param reason the reason phrase
   */
  public void setReasonPhrase(final String reason)
  {
    this.reason = reason;
  }

  /**
   * Get the reason phrase
   * 
   * @return the reason phrase
   */
  @Override
  public String toString()
  {
    String reason = this.reason;
    if (reason == null)
    {
      reason = this.status.getReasonPhrase();
    }
    return reason;
  }

  /**
   * Convert a numerical status code into the corresponding Status
   * 
   * @param statusCode
   *          the numerical status code
   * @return the matching Status or null is no matching Status is defined
   */
  public static Status fromStatusCode(final int statusCode)
  {
    Status status = null;
    for (Response.Status s : Response.Status.values())
    {
      if (s.getStatusCode() == statusCode)
      {
        status = s;
        break;
      }
    }
    return status;
  }
}
