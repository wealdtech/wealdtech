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

import com.google.common.collect.Ordering;
import com.wealdtech.TriVal;

/**
 * Allow ordering of TriVals.
 * <p>
 * Note that as the ordering chosen is arbitrary but consistent.  The ordering is absent(),
 * then clear(), then the natural ordering of the value held by the TriVal.
 */
public class TriValOrdering<T extends Comparable<T>> extends Ordering<TriVal<T>>
{
//  public static final TriValOrdering<?> INSTANCE = new TriValOrdering<T>();
//
//  public TriValOrdering()
//  {
//  }

  @Override
//  public int compare(final TriVal<? extends Comparable<?>> left, final TriVal<? extends Comparable<?>> right)
  public int compare(final TriVal<T> left, final TriVal<T> right)
  {
    int result = 0;
    if (left.isAbsent())
    {
      if (!right.isAbsent())
      {
        result = -1;
      }
    }
    else if (left.isClear())
    {
      if (right.isAbsent())
      {
        result = 1;
      }
      else if (right.isPresent())
      {
        result = -1;
      }
    }
    else
    {
      if (!right.isPresent())
      {
        return 1;
      }
      else
      {
        return left.get().compareTo(right.get());
      }
    }
    return result;
  }
}
