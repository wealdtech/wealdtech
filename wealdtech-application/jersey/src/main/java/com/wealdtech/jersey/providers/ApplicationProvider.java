/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.providers;

import com.sun.jersey.api.core.HttpContext;
import com.wealdtech.Application;
import com.wealdtech.ApplicationHeaders;
import com.wealdtech.jersey.exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * Provide the application ID to resource handlers. The application Id is set in the
 */
@Provider
public class ApplicationProvider extends AbstractInjectableProvider<Application>
{
  @Context
  private HttpServletRequest servletRequest;

  public ApplicationProvider()
  {
    super(Application.class);
  }

  /**
   * Provide the application ID.  If it is not available then throw an error
   */
  @Override
  public Application getValue(final HttpContext c)
  {
    final Application application = (Application)servletRequest.getAttribute("com.wealdtech.application");
    if (application == null)
    {
      throw new BadRequestException("Missing " + ApplicationHeaders.APPLICATION_HEADER + " header to determine application",
                                    "Cannot proceed without application information");
    }
    return application;
  }

}
