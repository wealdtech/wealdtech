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

import com.sun.jersey.api.core.HttpContext;
import com.wealdtech.utils.RequestHint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 */
@Provider
public class RequestHintProvider extends AbstractInjectableProvider<RequestHint>
{
  @Context
  private HttpServletRequest servletRequest;

  public RequestHintProvider()
  {
    super(RequestHint.class);
  }

  @Override
  public RequestHint getValue(final HttpContext c)
  {
    return (RequestHint)this.servletRequest.getAttribute("com.wealdtech.requesthint");
  }
}
