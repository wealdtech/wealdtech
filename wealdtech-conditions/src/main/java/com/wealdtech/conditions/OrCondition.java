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
public class OrCondition extends LogicalCondition
{
  private final LogicalCondition first;
  private final LogicalCondition second;

  public OrCondition(final LogicalCondition first, final LogicalCondition second)
  {
    this.first = first;
    this.second = second;
  }

  public LogicalCondition first() { return first; }

  public LogicalCondition second() { return second; }

  @Override
  public Boolean evaluate()
  {
    return first.evaluate() || second.evaluate();
  }
}
