/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Elements which exist in every chat object
 */
public abstract class ChatObject<T extends ChatObject<T>> extends WObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(ChatObject.class);

  public ChatObject(final Map<String, Object> data)
  {
    super(data);
  }

  protected void validate()
  {
    checkState(exists(ID), "Item ID is required");
  }

  @Override
  @JsonIgnore
  @Nonnull
  public WID<T> getId() { return super.getId(); }

  public static class Builder<T extends ChatObject<T>, P extends Builder<T, P>> extends WObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }
  }
}
