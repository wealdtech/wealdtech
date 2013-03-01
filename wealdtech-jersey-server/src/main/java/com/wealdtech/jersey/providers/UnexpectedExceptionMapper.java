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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.errors.ErrorInfo;
import com.wealdtech.jackson.ObjectMapperFactory;

/**
 * Convert unexpected exceptions in to a suitable JSON response for clients.
 * <p>We use the phrase "unexpected" here to indicate exceptions that are not
 * caught and translated in to a subclass of {@link com.wealdtech.jersey.exceptions.HttpException}.
 */
@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Exception>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);

  private static final transient ObjectMapper MAPPER = ObjectMapperFactory.getDefaultMapper();

  @Override
  public Response toResponse(final Exception exception)
  {
    LOGGER.info("Unexpected exception", exception);
    ResponseBuilder builder = Response.status(Status.BAD_REQUEST)
                                      .entity(defaultJSON(exception))
                                      .type(MediaType.APPLICATION_JSON);
    return builder.build();
  }

  private String defaultJSON(final Exception exception)
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
