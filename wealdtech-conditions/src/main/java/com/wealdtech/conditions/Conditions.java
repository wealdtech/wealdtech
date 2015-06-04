/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.conditions;

/**
 */
public class Conditions
{
  public static final True TRUE = new True();

  public static final False FALSE = new False();

  public static final LogicalCondition not(final Boolean value) { return new NotCondition(value); }

  public static final <T> LogicalCondition and(final LogicalCondition first, final LogicalCondition second) { return new AndCondition(first, second); }

  public static final <T> LogicalCondition or(final LogicalCondition first, final LogicalCondition second) { return new OrCondition(first, second); }

  public static final <T> LogicalCondition equal(final T first, final T second) { return new EqualCondition(first, second); }
}
