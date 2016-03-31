/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.joda.time.LocalDateTime;

import java.util.Map;

/**
 * A trace is a combination of subject and timeframe
 */
public class Trace extends WObject<Trace> implements Comparable<Trace>
{
  private static final String TYPE = "type";
  private static final String SUBJECT = "subject";
  private static final String REFERENCE = "reference";
  private static final String TIMESTAMP = "timestamp";

  @JsonCreator
  public Trace(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<String> getType() { return get(TYPE, String.class); }

  @JsonIgnore
  public Optional<String> getSubject() { return get(SUBJECT, String.class); }

  @JsonIgnore
  public Optional<String> getReference() { return get(REFERENCE, String.class); }

  @JsonIgnore
  public LocalDateTime getTimestamp() { return get(TIMESTAMP, LocalDateTime.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(TIMESTAMP)) { throw new DataError.Missing("Trace needs 'timestamp' information"); }
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Trace, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Trace prior)
    {
      super(prior);
    }

    public P type(final String type)
    {
      data(TYPE, type);
      return self();
    }

    public P subject(final String subject)
    {
      data(SUBJECT, subject);
      return self();
    }

    public P reference(final String reference)
    {
      data(REFERENCE, reference);
      return self();
    }

    public P timestamp(final LocalDateTime timestamp)
    {
      data(TIMESTAMP, timestamp);
      return self();
    }

    public Trace build()
    {
      return new Trace(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Trace prior)
  {
    return new Builder(prior);
  }
}
