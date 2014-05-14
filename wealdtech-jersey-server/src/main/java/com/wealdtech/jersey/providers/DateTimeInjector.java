/*
 * Copyright 2014 Weald Technology Trading Limited
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 */
@Provider
public class DateTimeInjector extends PerRequestTypeInjectableProvider<QueryParam, DateTime>
{
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ ZZZZ");
  private static final DateTimeFormatter DATE_TIME_FORMATTER_NO_TZ = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  private final UriInfo uriInfo;

  /**
   * Creates a new DateTimeInjector.
   * <p/>
   *
   * @param uriInfo an instance of {@link UriInfo}
   */
  public DateTimeInjector(@Context UriInfo uriInfo)
  {
    super(DateTime.class);
    this.uriInfo = uriInfo;
  }

  @Override
  public Injectable<DateTime> getInjectable(final ComponentContext cc, final QueryParam a)
  {
    return new Injectable<DateTime>()
    {
      @Override
      public DateTime getValue()
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
        DateTime result;
        if (txt.indexOf(' ') == -1)
        {
          // No timezone, use the no-tz formatter and UTC timezone
          result = DATE_TIME_FORMATTER_NO_TZ.parseDateTime(txt).withZone(DateTimeZone.UTC);
        }
        else
        {
          // Timezone supplied
          result = DATE_TIME_FORMATTER.parseDateTime(txt);
        }

        return result;
      }
    };
  }
}
