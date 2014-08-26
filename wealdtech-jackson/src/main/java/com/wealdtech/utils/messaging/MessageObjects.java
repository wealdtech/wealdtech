/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.DataError;
import com.wealdtech.utils.RequestHint;

import java.io.Serializable;

import static com.wealdtech.Preconditions.checkState;

/**
 * MessageObjects contain prior and current state of an object, along with an identifier related to the user who initiated the
 * change of state.  This can be used to pass around detailed state change information without having to use additional services
 * to obtain details of what has changed.
 */
public class MessageObjects<T extends Object> implements Serializable
{
  private static final long serialVersionUID = 6306799063373268531L;

  private final transient Long userId;
  private final transient RequestHint hint;
  private final transient T prior;
  private final transient T current;

  /**
   * Create a MessageObjects.
   * @param prior the prior state of the object, or <code>null</code> if there was not one
   * @param current the current state of the object, or <code>null</code> if there is not one
   * @throws DataError if the objects passed do not match a valid state
   */
  @JsonCreator
  public MessageObjects(@JsonProperty("userid") final Long userId,
                        @JsonProperty("hint") final RequestHint hint,
                        @JsonProperty("prior") final T prior,
                        @JsonProperty("current") final T current)
  {
    checkState(prior != null || current != null, "At least one object must be present");

    this.userId = userId;
    this.hint = hint;
    this.prior = prior;
    this.current = current;
  }

  /**
   * Obtain the ID of the user who initiated the change of state of the object.
   */
  public Long getUserId()
  {
    return this.userId;
  }

  /**
   * Obtain hints supplied as part of the request which generated this message
   * @return the hint
   */
  public RequestHint getHint() { return this.hint; }

  /**
   * Obtain the prior state of the object.
   * @return the prior state of the object, or <code>null</code> if there was not one
   */
  public T getPrior()
  {
    return this.prior;
  }

  /**
   * Obtain the current state of the object.
   * @return the current state of the object, or <code>null</code> if there is not one
   */
  public T getCurrent()
  {
    return this.current;
  }

  /**
   * Obtain the type of the object.
   * @return the class of the object
   */
  public Class<? extends Object> getType()
  {
    Class<? extends Object> retclass;
    if (this.prior == null)
    {
      retclass = this.current.getClass();
    }
    else
    {
      retclass = this.prior.getClass();
    }
    return retclass;
  }
}