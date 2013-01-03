/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.wealdtech.jersey.exceptions;

import javax.ws.rs.core.Response.Status;

import com.google.common.base.Optional;

/**
 * Base class for daemon HTTP exceptions that provide additional information to
 * the requestor.
 */
public class HttpException extends RuntimeException
{
  private final Status status;
  private final Optional<Integer> retryAfter;

  public HttpException(final Status status, final String message, final Integer retryAfter, final Throwable t)
  {
    super(message, t);
    this.status = status;
    this.retryAfter = Optional.fromNullable(retryAfter);
  }

  public HttpException(final Status status, final String message, final Throwable t)
  {
    super(message, t);
    this.status = status;
    this.retryAfter = Optional.absent();
  }

  public HttpException(final Throwable t)
  {
    super(t);
    this.status = Status.BAD_REQUEST;
    this.retryAfter = Optional.absent();
  }

  public Status getStatus()
  {
    return this.status;
  }

  public Optional<Integer> getRetryAfter()
  {
    return this.retryAfter;
  }
}

//public class HttpException extends WebApplicationException
//{
//  private static final long serialVersionUID = 4060298101492380582L;
//  private static final Logger LOGGER = LoggerFactory.getLogger(HttpException.class);
//
//  private static transient final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
//  private static transient final int DEFAULTRETRY = 60;
//
//  // TODO provide fallback output
//  private static transient final String FALLBACKOUTPUT = "{\"developermessage\":\"developer message goes here\",\"usermessage\":\"user message goes here\",\"errorcode\":\"\",\"moreinfo\":\"more info goes here\"}";
//
//  private transient final int retryAfter;
//
//  public HttpException(final Response response, final int retryAfter)
//  {
//    super(response);
//    this.retryAfter = retryAfter;
//  }
//
//  public HttpException(final Response response)
//  {
//    this(response, DEFAULTRETRY);
//  }
//
//  public HttpException(final Status status,
//                       final String errorCode,
//                       final Integer retryAfter,
//                       final Throwable t)
//  {
//    this(Response.status(status)
//                 .type(MediaType.APPLICATION_JSON)
//                 .entity(statusToJSON(errorCode))
//                 .build(),
//                 retryAfter);
//  }
//
//  public HttpException(final Status status,
//                       final String errorCode,
//                       final Throwable t)
//  {
//    this(Response.status(status)
//                 .type(MediaType.APPLICATION_JSON)
//                 .entity(statusToJSON(errorCode))
//                 .build());
//  }
//
//  private static String statusToJSON(final String errorCode)
//  {
//    HttpStatus status = HttpExceptionMap.get(errorCode);
//    try
//    {
//      return mapper.writeValueAsString(status);
//    }
//    catch (JsonProcessingException e)
//    {
//      LOGGER.error("Failed to generate JSON for status \"{}\"", status);
//      return FALLBACKOUTPUT;
//    }
//  }
//
//  public Integer getRetryAfter()
//  {
//    return this.retryAfter;
//  }
//}
