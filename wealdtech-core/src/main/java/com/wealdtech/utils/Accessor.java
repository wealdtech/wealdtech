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

package com.wealdtech.utils;


/**
 * Accessors allow arbitrary access to a set of data.
 */
public interface Accessor<T, M>
{
  public void setBaseItem(final T mark);

  public void setBase(final M mark);

  public boolean hasNext();

  public T next();

  public T nextAfterItem(final T mark);

  public T nextAfter(final M mark);

  public boolean hasPrevious();

  public T previous();

  public T previousBeforeItem(final T mark);

  public T previousBefore(final M mark);
}
