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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.wealdtech.jackson.WealdMapper;

/**
 * Provide an objectmapper.
 * <p/>
 * The default objectmapper will be provided, unless an object mapper
 * configuration is passed in to the constructor for this provider in which case
 * those configuration options will override the default.
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper>
{
  private transient final ObjectMapper mapper;

  @Context
  private transient HttpServletRequest servletrequest;

  @Inject
  public ObjectMapperProvider()
  {
//    this.mapper = ObjectMapperFactory.getDefaultMapper();
    this.mapper = WealdMapper.getServerMapper();
  }

  @Override
  public ObjectMapper getContext(final Class<?> type)
  {
    return this.mapper;
  }
}
