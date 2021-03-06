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

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.sun.jersey.api.container.ContainerException;

/**
 * Catch ContainerExceptions and respond with the underlying cause.
 */
@Provider
public class ContainerExceptionMapper implements ExceptionMapper<ContainerException>
{
  @Context
  private Providers providers;

  @SuppressWarnings("unchecked")
  @Override
  public Response toResponse(final ContainerException exception)
  {
    Throwable underlying = exception.getCause();

    return ((ExceptionMapper<Throwable>)this.providers.getExceptionMapper(underlying.getClass())).toResponse(underlying);
  }
}
