/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.wealdtech.WID;
import com.wealdtech.WObject;

/**
 * Interface defining WObject service methods
 */
public interface WObjectService<T extends WObject, U>
{
  /**
   * Create the service's datastore.
   * If the datastore already exists then this does nothing; any data which was previously in the datastore will still be there
   * after this call completes.
   */
  public void createDatastore();

  /**
   * Destroy the service's datastore.
   * This is a destructive operation; when this is called it is expected that any existing data for the chat service will be
   * removed
   */
  public void destroyDatastore();

  /**
   * Add a object
   */
  public void add(T item);

  /**
   * Update an object.  The item to be updated is defined by the ID of the item provided
   */
  public void update(T item);

  /**
   * Update an object.  The object to be updated is defined by the callback provided
   */
  public void update(T item, WObjectServiceCallback<U> cb);

  /**
   * Remove an object
   */
  public void remove(WID<T> itemId);

  /**
   * Remove multiple objects based on callback conditions
   */
  public void remove(WObjectServiceCallback<U> cb);

  /**
   * Obtain objects
   */
  public ImmutableList<T> obtain(TypeReference<T> typeRef, WObjectServiceCallback<U> cb);

  /**
   * Run a generic query
   */
  public <V> ImmutableList<V> query(TypeReference<V> typeRef, WObjectServiceCallback<U> cb);
}
