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

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import com.wealdtech.jackson.modules.DateTimeZoneDeserializer;
import org.joda.time.DateTimeZone;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Allow injection of {@code DateTimeZone} directly as query parameters
 */
@Provider
public class DateTimeZoneInjector extends PerRequestTypeInjectableProvider<QueryParam, DateTimeZone>
{
  private final UriInfo uriInfo;

  /**
   * Creates a new DateTimeZoneInjector.
   *
   * @param uriInfo an instance of {@link javax.ws.rs.core.UriInfo}
   */
  public DateTimeZoneInjector(@Context UriInfo uriInfo)
  {
    super(DateTimeZone.class);
    this.uriInfo = uriInfo;
  }

  @Override
  public Injectable<DateTimeZone> getInjectable(final ComponentContext cc, final QueryParam a)
  {
    return new Injectable<DateTimeZone>()
    {
      @Override
      public DateTimeZone getValue()
      {
        final List<String> values = uriInfo.getQueryParameters().get(a.value());
        if (values == null || values.isEmpty())
        {
          return null;
        }
        if (values.size() > 1)
        {
          throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                    .entity(a.value() + " may only contain a single value")
                                                    .build());
        }
        final String txt = values.get(0);
        try
        {
          return DateTimeZoneDeserializer.deserialize(txt);
        }
        catch (IllegalArgumentException e)
        {
          throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                    .entity(a.value() + " contains invalid value")
                                                    .build());
        }
      }
    };
  }
}
