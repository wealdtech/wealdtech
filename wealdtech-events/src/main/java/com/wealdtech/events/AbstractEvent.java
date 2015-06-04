/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.events;

import com.wealdtech.WObject;

/**
 * The abstract event.  This contains a type and a body
 */
public abstract class AbstractEvent<T extends WObject<?>>
{
  private Enum type;
  private T body;

  protected AbstractEvent(final Enum type, final T body)
  {
    this.type = type;
    this.body = body;
  }

  public Enum getType()
  {
    return type;
  }

  public T getBody()
  {
    return body;
  }
}
