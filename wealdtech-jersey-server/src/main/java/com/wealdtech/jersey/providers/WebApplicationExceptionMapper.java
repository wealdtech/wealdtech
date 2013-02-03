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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert Jersey exceptions in to a suitable JSON response for clients.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  @Override
  public Response toResponse(final WebApplicationException exception)
  {
    LOGGER.debug(exception.getMessage());
    return Response.status(exception.getResponse().getStatus())
                   .entity("")
                   .type(MediaType.APPLICATION_JSON)
                   .build();
  }
}
