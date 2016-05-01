/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.rekt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Additional information provided to result generators and validators
 */
public class AdditionalInfo extends WObject<AdditionalInfo> implements Comparable<AdditionalInfo>
{
  private static final Logger LOG = LoggerFactory.getLogger(AdditionalInfo.class);

  @JsonCreator
  public AdditionalInfo(final Map<String, Object> data)
  {
    super(data);
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<AdditionalInfo, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final AdditionalInfo prior)
    {
      super(prior);
    }

    public AdditionalInfo build()
    {
      return new AdditionalInfo(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final AdditionalInfo prior)
  {
    return new Builder(prior);
  }
}
