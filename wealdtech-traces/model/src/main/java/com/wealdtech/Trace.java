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
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.activities.Activity;
import com.wealdtech.contexts.Context;
import org.joda.time.LocalDateTime;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;
import static sun.security.x509.X509CertInfo.SUBJECT;

/**
 * A trace is a combination of subject and timeframe
 */
public class Trace extends WObject<Trace> implements Comparable<Trace>
{
  private static final String CONTEXTS = "contexts";
  private static final String ACTIVITIES = "activities";
  private static final String TIMESTAMP = "timestamp";

  @JsonCreator
  public Trace(final Map<String, Object> data)
  {
    super(data);
  }

  private static final TypeReference<ImmutableSet<Activity>> ACTIVITIES_TYPE_REF = new TypeReference<ImmutableSet<Activity>>() {};

  @JsonIgnore
  public ImmutableSet<Activity> getActivities(){ return get(ACTIVITIES, ACTIVITIES_TYPE_REF).or(ImmutableSet.<Activity>of()); }

  @JsonIgnore
  public Optional<String> getSubject(){ return get(SUBJECT, String.class); }

  private static final TypeReference<ImmutableSet<Context>> CONTEXTS_TYPE_REF = new TypeReference<ImmutableSet<Context>>() {};

  @JsonIgnore
  public ImmutableSet<Context> getContexts(){ return get(CONTEXTS, CONTEXTS_TYPE_REF).or(ImmutableSet.<Context>of()); }

  @JsonIgnore
  public LocalDateTime getTimestamp(){ return get(TIMESTAMP, LocalDateTime.class).get(); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TIMESTAMP), "Trace failed validation: missing timestamp");
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

    public P activities(final ImmutableSet<Activity> activities)
    {
      data(ACTIVITIES, activities);
      return self();
    }

    public P subject(final String subject)
    {
      data(SUBJECT, subject);
      return self();
    }

    public P contexts(final ImmutableSet<Context> contexts)
    {
      data(CONTEXTS, contexts);
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
