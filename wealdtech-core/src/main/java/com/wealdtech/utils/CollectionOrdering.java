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

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * Allow ordering of Collections.
 * <p>
 * Note that as the ordering chosen is arbitrary but consistent.  The ordering is dependent on
 * the number of items in each collection, then by comparison of individual elements after sorting.
 */
public class CollectionOrdering<Collection<T extends Comparable<T>>> extends Ordering<Collection<T>>
{
  public CollectionOrdering()
  {
  }

  @Override
  public int compare(final T left, final T right)
  {
    int result = 0;
    if (left == null)
    {
      if (right != null)
      {
        result = -1;
      }
    }
    else
    {
      if (right == null)
      {
        result = 1;
      }
      else
      {
        int sizeDiff = left.size() - right.size();
        if (sizeDiff < 0)
        {
          result = -1;
        }
        else if (sizeDiff > 0)
        {
          result = 1;
        }
        else
        {
          // Sort the individual elements and compare them
          final ImmutableList<Comparable<?>> sortedLeft = ImmutableList.copyOf(left);
          final ImmutableList<Comparable<?>> sortedRight = ImmutableList.copyOf(right);
          for (int i = 0; i < sortedLeft.size(); i++)
          {
            result = sortedLeft.get(i).compareTo(sortedRight.get(i));
            if (result != 0)
            {
              break;
            }
          }
        }
      }
    }
    return result;
  }
}
