/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 */
public class RequestLogEntry extends WObject<RequestLogEntry>
{
  private static final String TIMESTAMP = "timestamp";
  private static final String SOURCE = "source";
  private static final String METHOD = "method";
  private static final String PATH = "path";
  private static final String STATUS = "status";
  private static final String DURATION = "duration";

  @JsonCreator
  public RequestLogEntry(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(TIMESTAMP), "Request log entry failed validation: requires timestamp");
    checkState(exists(SOURCE), "Request log entry failed validation: requires source");
    checkState(exists(METHOD), "Request log entry failed validation: requires method");
    checkState(exists(PATH), "Request log entry failed validation: requires path");
    checkState(exists(STATUS), "Request log entry failed validation: requires status");
    checkState(exists(DURATION), "Request log entry failed validation: requires duration");
  }

  @JsonIgnore
  public Long getTimestamp() { return get(TIMESTAMP, Long.class).get(); }

  @JsonIgnore
  public String getSource() { return get(SOURCE, String.class).get(); }

  @JsonIgnore
  public String getMethod() { return get(METHOD, String.class).get(); }

  @JsonIgnore
  public String getPath() { return get(PATH, String.class).get(); }

  @JsonIgnore
  public Integer getStatus() { return get(STATUS, Integer.class).get(); }

  @JsonIgnore
  public Long getDuration() { return get(DURATION, Long.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<RequestLogEntry, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final RequestLogEntry prior)
    {
      super(prior);
    }

    public P timestamp(final Long timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public P source(final String source)
    {
      data(SOURCE, source);
      return self();
    }

    public P method(final String method)
    {
      data(METHOD, method);
      return self();
    }

    public P path(final String path)
    {
      data(PATH, path);
      return self();
    }

    public P status(final Integer status)
    {
      data(STATUS, status);
      return self();
    }

    public P duration(final Long duration)
    {
      data(DURATION, duration);
      return self();
    }

    public RequestLogEntry build()
    {
      return new RequestLogEntry(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final RequestLogEntry prior)
  {
    return new Builder(prior);
  }
}
