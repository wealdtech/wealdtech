/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.wealdtech.Continuum;
import com.wealdtech.TwoTuple;

/**
 */
public class IntegerContinuum extends Continuum<Integer>
{
  public static final ImmutableList<TwoTuple<Range<Integer>, String>> ranges = ImmutableList.of(new TwoTuple<>(Range.<Integer>openClosed(0, 100), "Low"),
                                                                                                new TwoTuple<>(Range.<Integer>openClosed(100, 200), "Medium"),
                                                                                                new TwoTuple<>(Range.<Integer>atLeast(200), "High"));

  public IntegerContinuum(final Integer level)
  {
    super(ranges, level);
  }
}
