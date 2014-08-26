/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.providers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.wealdtech.errors.ErrorInfo;
import com.wealdtech.jackson.ObjectMapperFactory;

/**
 * Catch errors with JSON mapping and re-phrase them.
 * <p>
 * This usually occurs because a user has missed a required value or supplied invalid information so we pull the underlying
 * message and use that.
 */
@Provider
public class JsonMappingExceptionManager implements ExceptionMapper<JsonMappingException>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonMappingExceptionManager.class);

  private static final transient ObjectMapper MAPPER = ObjectMapperFactory.getDefaultMapper();

  @Override
  public Response toResponse(final JsonMappingException exception)
  {
    LOGGER.info("JSON mapping exception", exception);
    ResponseBuilder builder = Response.status(Status.BAD_REQUEST)
                                      .entity(defaultJSON(Objects.firstNonNull(exception.getCause(), exception)))
                                      .type(MediaType.APPLICATION_JSON);
    return builder.build();
  }

  private String defaultJSON(final Throwable exception)
  {
    ErrorInfo errorInfo = new ErrorInfo(null, exception.getMessage(), exception.getMessage(), (String)null);

    try
    {
      return MAPPER.writeValueAsString(errorInfo);
    }
    catch (JsonProcessingException e)
    {
      LOGGER.error("Failed to generate JSON for status \"{}\"", errorInfo);
      return "{\"message\":\"An error occurred\"}";
    }
  }
}
