/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.net.InetAddresses;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.wealdtech.utils.RequestHint;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Pick various items out of the request headers to make available to resources
 */
public class RequestHintFilter implements ContainerRequestFilter
{
  private static final Logger LOG = LoggerFactory.getLogger(RequestHintFilter.class);

  // A cache containing mappings from language strings (e.g. en-GB) to locales
  private transient final LoadingCache<String, Locale> locales = CacheBuilder.newBuilder()
                                                                               .maximumSize(1000)
                                                                               .build(new CacheLoader<String, Locale>()
                                                                               {
                                                                                 public Locale load(String name)
                                                                                 {
                                                                                   if (name == null)
                                                                                   {
                                                                                     return null;
                                                                                   }
                                                                                   name = name.trim();
                                                                                   if (name.toLowerCase().equals("default"))
                                                                                   {
                                                                                     return Locale.getDefault();
                                                                                   }

                                                                                   // Extract language
                                                                                   int languageIndex = name.indexOf('_');
                                                                                   String language = null;
                                                                                   if (languageIndex == -1)
                                                                                   {
                                                                                     // No further "_" so is "{language}" only
                                                                                     return new Locale(name, "");
                                                                                   }
                                                                                   else
                                                                                   {
                                                                                     language = name.substring(0, languageIndex);
                                                                                   }

                                                                                   // Extract country
                                                                                   int countryIndex = name.indexOf('_', languageIndex + 1);
                                                                                   String country = null;
                                                                                   if (countryIndex == -1)
                                                                                   {
                                                                                     // No further "_" so is "{language}_{country}"
                                                                                     country = name.substring(languageIndex+1);
                                                                                     return new Locale(language, country);
                                                                                   }
                                                                                   else
                                                                                   {
                                                                                     // Assume all remaining is the variant so is "{language}_{country}_{variant}"
                                                                                     country = name.substring(languageIndex+1, countryIndex);
                                                                                     String variant = name.substring(countryIndex+1);
                                                                                     return new Locale(language, country, variant);
                                                                                   }                                                                                 }
                                                                               });

  @Context
  HttpServletRequest req;

  @Override
  public ContainerRequest filter(final ContainerRequest request)
  {
    final RequestHint.Builder builder = RequestHint.builder();

    builder.userAgent(request.getHeaderValue("User-Agent"));

    final String timezone = request.getHeaderValue("Timezone");
    if (timezone != null)
    {
      try
      {
        builder.timezone(DateTimeZone.forID(timezone));
      }
      catch (final IllegalArgumentException iae)
      {
        LOG.warn("Unrecognised timezone {}", timezone);
      }
    }

    final String geoPosition = request.getHeaderValue("Geo-Position");
    if (geoPosition != null)
    {
      // We only care about items prior to the space (if there is one)
      final Iterator<String> geoItems;
      if (geoPosition.indexOf(' ') != -1)
      {
        geoItems = Splitter.on(";").split(Splitter.on(" ").split(geoPosition).iterator().next()).iterator();
      }
      else
      {
        geoItems = Splitter.on(";").split(geoPosition).iterator();
      }

      if (geoItems.hasNext())
      {
        final String lat = geoItems.next();
        builder.latitude(Float.valueOf(lat));
      }
      if (geoItems.hasNext())
      {
        final String lng = geoItems.next();
        builder.longitude(Float.valueOf(lng));
      }
      if (geoItems.hasNext())
      {
        final String alt = geoItems.next();
        builder.altitude(Float.valueOf(alt));
      }
    }

    final List<Locale> languages = request.getAcceptableLanguages();
    if (languages != null && !languages.isEmpty() && !languages.get(0).equals("*"))
    {
      builder.locale(languages.get(0));
    }

    try
    {
      builder.address(InetAddresses.forString(req.getRemoteAddr()));
    }
    catch (final IllegalArgumentException ignored)
    {
      LOG.debug("Remote IP address {} could not be parsed", req.getRemoteAddr());
    }

    // Store our completed hint for access by resources
    this.req.setAttribute("com.wealdtech.requesthint", builder.build());

    return request;
  }
}
