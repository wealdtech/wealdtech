/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.pushwoosh;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.DataError;
import com.wealdtech.WObject;

import java.util.Map;

/**
 */
public class PushWooshBody extends WObject<PushWooshBody>
{
  private static final String REQUEST = "request";

  @JsonCreator
  public PushWooshBody(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected void validate()
  {
    if (!exists(REQUEST))
    {
      throw new DataError.Missing("PushWoosh body failed validation: requires request");
    }
  }

  @JsonIgnore
  public PushWooshRequest getRequest() { return get(REQUEST, PushWooshRequest.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<PushWooshBody, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final PushWooshBody prior)
    {
      super(prior);
    }

    public P request(final PushWooshRequest request)
    {
      data(REQUEST, request);
      return self();
    }

    public PushWooshBody build()
    {
      return new PushWooshBody(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final PushWooshBody prior)
  {
    return new Builder(prior);
  }
}
