/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
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
import org.joda.time.DateTime;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 */
public class GitHubStatus extends WObject<GitHubStatus> implements Comparable<GitHubStatus>
{
  private static final String STATUS = "status";
  private static final String LAST_UPDATED = "last_updated";

  @JsonCreator
  public GitHubStatus(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(STATUS), "GitHub status failed validation: must contain status (" + getAllData() + ")");
    checkState(exists(LAST_UPDATED), "GitHub status failed validation: must contain last updated (" + getAllData() + ")");
  }

  @JsonIgnore
  public String getStatus(){ return get(STATUS, String.class).get(); }

  @JsonIgnore
  public DateTime getLastUpdated(){ return get(LAST_UPDATED, DateTime.class).get(); }
}
