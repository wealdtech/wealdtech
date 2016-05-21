/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import com.wealdtech.DataError;
import com.wealdtech.errors.ErrorInfo;
import com.wealdtech.jackson.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handle Weald data errors, mapping them to standard HTTP error codes
 */
@Provider
@Singleton
public class DataErrorExceptionMapper implements ExceptionMapper<DataError>
{
  private static final Logger LOG = LoggerFactory.getLogger(DataErrorExceptionMapper.class);

  private static final ObjectMapper MAPPER = ObjectMapperFactory.getDefaultMapper();

  @Override
  public Response toResponse(final DataError exception)
  {
    LOG.error("Caught data error ", exception);


    final Response.ResponseBuilder builder;
    if (exception instanceof DataError.Authentication)
    {
      builder = Response.status(Response.Status.UNAUTHORIZED)
                        .entity(defaultJson(exception))
                        .type(MediaType.APPLICATION_JSON);
    }
    else if (exception instanceof DataError.Bad)
    {
      builder = Response.status(Response.Status.BAD_REQUEST)
                        .entity(defaultJson(exception))
                        .type(MediaType.APPLICATION_JSON);
    }
    else if (exception instanceof DataError.Missing)
    {
      builder = Response.status(Response.Status.NOT_FOUND)
                        .entity(defaultJson(exception))
                        .type(MediaType.APPLICATION_JSON);
    }
    else if (exception instanceof DataError.Permission)
    {
      builder = Response.status(Response.Status.UNAUTHORIZED)
                        .entity(defaultJson(exception))
                        .type(MediaType.APPLICATION_JSON);
    }
    else
    {
      LOG.warn("Unhandled data error ", exception);
      builder = Response.status(Response.Status.BAD_REQUEST)
                        .entity(defaultJson(exception))
                        .type(MediaType.APPLICATION_JSON);
    }

    return builder.build();
  }

  private String defaultJson(final DataError exception)
  {
    final ErrorInfo errorInfo = new ErrorInfo(null, exception.getMessage(), exception.getMessage(), (String)null);

    try
    {
      return MAPPER.writeValueAsString(errorInfo);
    }
    catch (final JsonProcessingException e)
    {
      LOG.error("Failed to generate JSON for status \"{}\"", errorInfo);
      return "{\"message\":\"An error occurred\"}";
    }
  }
}