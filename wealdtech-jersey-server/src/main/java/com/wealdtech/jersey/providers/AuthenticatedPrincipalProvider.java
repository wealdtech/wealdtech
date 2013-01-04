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

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.sun.jersey.api.core.HttpContext;

/**
 * Provide details of an authenticated principal. Most of the heavy lifting is
 * carried out by the authentication filter, so we're just here as a convenience
 * to make the information easily accessible.
 *
 * @param <E>
 */
//@Provider
public class AuthenticatedPrincipalProvider<E> extends AbstractInjectableProvider<E>
{
  @Context
  private transient HttpServletRequest servletrequest;

  public AuthenticatedPrincipalProvider(final Type t)
  {
    super(t);
  }

  @Override
  public E getValue(final HttpContext c)
  {
    return (E)this.servletrequest.getAttribute("com.wealdtech.principal");
  }
}
