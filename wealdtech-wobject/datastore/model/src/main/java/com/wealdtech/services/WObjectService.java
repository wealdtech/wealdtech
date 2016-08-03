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
   * Add an item
   * @param item the item to add
   */
  public void add(T item);

  /**
   * Update an item.  The item to be updated is defined by the ID of the item provided
   * @param item the item to update
   */
  public void update(T item);

  /**
   * Update an item.  The item to be updated is defined by the callback provided
   * @param item the item
   * @param cb the callback defining the item to update
   */
  public void update(T item, WObjectServiceCallback<U> cb);

  /**
   * Remove an item
   * @param itemId the ID of the item to remove
   */
  public void remove(WID<T> itemId);

  /**
   * Remove multiple items based on callback conditions
   * @param cb the callback defining the item(s) to remove
   */
  public void remove(WObjectServiceCallback<U> cb);

  /**
   * Obtain an item
   * @param klazz the class of the item to obtain
   * @param itemId the ID of the item to obtain
   * @return the item; can be {@code null}
   */
  public T obtain(Class<T> klazz, WID<T> itemId);

  /**
   * Obtain an item
   * @param typeRef the type reference of the item to obtain
   * @param itemId the ID of the item to obtain
   * @return the item; can be {@code null}
   */
  public T obtain(TypeReference<T> typeRef, WID<T> itemId);

  /**
   * Obtain items
   * @param typeRef the type reference of the items to obtain
   * @param cb the callback defining the item(s) to obtain
   * @return a list of items
   */
  public ImmutableList<T> obtain(TypeReference<T> typeRef, WObjectServiceCallback<U> cb);

  /**
   * Run a generic query to obtain items
   * @param typeRef the type reference of the items to obtain
   * @param cb the callback defining the item(s) to obtain
   * @param <V> the type of the items to return
   * @return a list of items
   */
  public <V> ImmutableList<V> query(TypeReference<V> typeRef, WObjectServiceCallback<U> cb);
}
