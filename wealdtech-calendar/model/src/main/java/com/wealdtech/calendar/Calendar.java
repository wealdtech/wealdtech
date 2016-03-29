/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

/**
 * A calendar
 */
public class Calendar extends WObject<Calendar> implements Comparable<Calendar>
{
  private static final String REMOTE_ID = "remoteid";
  private static final String SUMMARY = "summary";
  private static final String DESCRIPTION = "description";

  @JsonCreator
  public Calendar(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<String> getRemoteId() { return get(REMOTE_ID, String.class); }

  @JsonIgnore
  public String getSummary() { return get(SUMMARY, String.class).get(); }

  @JsonIgnore
  public Optional<String> getDescription() { return get(DESCRIPTION, String.class); }

  @Override
  protected void validate()
  {
    super.validate();
    if (!exists(SUMMARY)) { throw new DataError.Missing("Event needs 'summary' information"); }
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<Calendar, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Calendar prior)
    {
      super(prior);
    }

    public P remoteId(final String remoteId)
    {
      data(REMOTE_ID, remoteId);
      return self();
    }

    public P summary(final String summary)
    {
      data(SUMMARY, summary);
      return self();
    }

    public P description(final String description)
    {
      data(DESCRIPTION, description);
      return self();
    }

    public Calendar build()
    {
      return new Calendar(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Calendar prior)
  {
    return new Builder(prior);
  }

  }
