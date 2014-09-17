/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.joda.time.DateTimeZone;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.util.Locale;

/**
 * Information about a request which provides additional context
 */
public class RequestHint implements Comparable<RequestHint>
{
  private final Optional<Integer> latitude;
  private final Optional<Integer> longitude;
  private final Optional<Float> altitude;
  private final Optional<InetAddress> address;
  private final Optional<String> userAgent;
  private final Optional<Locale> locale;
  private final Optional<DateTimeZone> timezone;

  @JsonCreator
  public RequestHint(@JsonProperty("latitude") final Integer latitude,
                     @JsonProperty("longitude") final Integer longitude,
                     @JsonProperty("altitude") final Float altitude,
                     @JsonProperty("address") final InetAddress address,
                     @JsonProperty("useragent") final String userAgent,
                     @JsonProperty("locale") final Locale locale,
                     @JsonProperty("timezone") final DateTimeZone timezone)

  {
    this.latitude = Optional.fromNullable(latitude);
    this.longitude = Optional.fromNullable(longitude);
    this.altitude = Optional.fromNullable(altitude);
    this.address = Optional.fromNullable(address);
    this.userAgent = Optional.fromNullable(userAgent);
    this.locale = Optional.fromNullable(locale);
    this.timezone = Optional.fromNullable(timezone);
  }

  public Optional<Integer> getLatitude()
  {
    return this.latitude;
  }

  public Optional<Integer> getLongitude()
  {
    return this.longitude;
  }

  public Optional<Float> getAltitude()
  {
    return this.altitude;
  }

  public Optional<InetAddress> getAddress()
  {
    return this.address;
  }

  public Optional<String> getUserAgent()
  {
    return this.userAgent;
  }

  public Optional<Locale> getLocale()
  {
    return locale;
  }

  public Optional<DateTimeZone> getTimezone()
  {
    return timezone;
  }

  // Standard object methods
  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
                  .add("latitude", this.latitude.orNull())
                  .add("longitude", this.longitude.orNull())
                  .add("altitude", this.altitude.orNull())
                  .add("address", this.address.orNull())
                  .add("userAgent", this.userAgent.orNull())
                  .add("locale", this.locale.orNull())
                  .add("timezone", this.timezone.orNull())
                  .omitNullValues()
                  .toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    return that instanceof RequestHint && this.compareTo((RequestHint)that) == 0;
  }

  public int compareTo(@Nonnull final RequestHint that)
  {
    return ComparisonChain.start()
                          .compare(this.latitude.orNull(), that.latitude.orNull(), Ordering.natural().nullsFirst())
                          .compare(this.longitude.orNull(), that.longitude.orNull(), Ordering.natural().nullsFirst())
                          .compare(this.altitude.orNull(), that.altitude.orNull(), Ordering.natural().nullsFirst())
                          .compare(this.address.toString(), that.address.toString(), Ordering.natural().nullsFirst())
                          .compare(this.userAgent.orNull(), that.userAgent.orNull(), Ordering.natural().nullsFirst())
                          .compare(this.locale.isPresent() ? this.locale.get().toString() : null, that.locale.isPresent() ? that.locale.get().toString() : null, Ordering.natural().nullsFirst())
                          .compare(this.timezone.isPresent() ? this.timezone.get().toString() : null, that.timezone.isPresent() ? that.timezone.get().toString() : null, Ordering.natural().nullsFirst())
                          .result();
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.latitude, this.longitude, this.altitude, this.address, this.userAgent, this.locale, this.timezone);
  }

  // Builder boilerplate
  public static class Builder
  {
    private Integer latitude;
    private Integer longitude;
    private Float altitude;
    private InetAddress address;
    private String userAgent;
    private Locale locale;
    private DateTimeZone timezone;

    public Builder()
    {
      // Nothing to do
    }

    public Builder(final RequestHint prior)
    {
      this.latitude = prior.latitude.orNull();
      this.longitude = prior.longitude.orNull();
      this.altitude = prior.altitude.orNull();
      this.address = prior.address.orNull();
      this.userAgent = prior.userAgent.orNull();
      this.locale = prior.locale.orNull();
      this.timezone = prior.timezone.orNull();
    }

    public Builder latitude(final Integer latitude)
    {
      this.latitude = latitude;
      return this;
    }

    public Builder latitude(final float latitude)
    {
      this.latitude = (int)(latitude * 1000000);
      return this;
    }

    public Builder latitude(final double latitude)
    {
      this.latitude = (int)(latitude * 1000000);
      return this;
    }

    public Builder longitude(final Integer longitude)
    {
      this.longitude = longitude;
      return this;
    }

    public Builder longitude(final float longitude)
    {
      this.longitude = (int)(longitude * 1000000);
      return this;
    }

    public Builder longitude(final double longitude)
    {
      this.longitude = (int)(longitude * 1000000);
      return this;
    }

    public Builder altitude(final float altitude)
    {
      this.altitude = altitude;
      return this;
    }

    public Builder address(final InetAddress address)
    {
      this.address = address;
      return this;
    }

    public Builder userAgent(final String userAgent)
    {
      this.userAgent = userAgent;
      return this;
    }

    public Builder locale(final Locale locale)
    {
      this.locale = locale;
      return this;
    }

    public Builder timezone(final DateTimeZone timezone)
    {
      this.timezone = timezone;
      return this;
    }

    public RequestHint build()
    {
      return new RequestHint(this.latitude, this.longitude, this.altitude, this.address, this.userAgent, this.locale, this.timezone);
    }
  }

  public static Builder builder()
  {
    return new Builder();
  }

  public static Builder builder(final RequestHint prior)
  {
    return new Builder(prior);
  }
}
