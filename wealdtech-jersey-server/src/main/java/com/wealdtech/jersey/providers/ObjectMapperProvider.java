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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.HttpContext;
import com.wealdtech.jackson.WealdMapper;

import javax.ws.rs.ext.Provider;

/**
 * Provide an objectmapper.
 * <p>
 * The default objectmapper will be provided, unless an object mapper
 * configuration is passed in to the constructor for this provider in which case
 * those configuration options will override the default.
 */
@Provider
public class ObjectMapperProvider extends AbstractInjectableProvider<ObjectMapper>
{
  public ObjectMapperProvider()
  {
    super(ObjectMapper.class);
  }

  /**
   * Provide object mapper.
   */
  @Override
  public ObjectMapper getValue(final HttpContext c)
  {
    return WealdMapper.getServerMapper();
  }
}
