/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.utils.messaging;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.DataError;

/**
 * MessageObjects contain prior and current state of an object.
 * This can be used to pass around detailed state change information
 * without having to use additional services to obtain details of
 * what has changed.
 */
public class MessageObjects<T extends Object> implements Serializable
{
  private static final long serialVersionUID = 6306799063373268531L;

  private transient final T prior;
  private transient final T current;

  /**
   * Create a MessageObjects.
   * @param prior the prior state of the object, or <code>null</code> if there was not one
   * @param current the current state of the object, or <code>null</code> if there is not one
   * @throws DataError if the objects passed do not match a valid state
   */
  @JsonCreator
  public MessageObjects(final @JsonProperty("prior") T prior,
                        final @JsonProperty("current") T current) throws DataError
  {
    if ((prior == null) && (current == null))
    {
      throw new DataError("At least one object must be non-NULL");
    }
    this.prior = prior;
    this.current = current;
  }

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
    if (prior == null)
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