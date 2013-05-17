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

package com.wealdtech.schedule;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * An occurrence is a single specific occurrence of a
 * {@link Schedule}.  It specifies a concrete start
 * and end date.
 */
public class Occurrence implements Comparable<Occurrence>
{
  private final transient DateTime start;
  private final transient DateTime end;

  public Occurrence(final DateTime start, final DateTime end)
  {
    this.start = start;
    this.end = end;
  }

  public DateTime getStart()
  {
    return this.start;
  }

  public DateTime getEnd()
  {
    return this.end;
  }

  // Standard object methods

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("start", this.start)
                  .add("end", this.end)
                  .toString();
  }

  @Override
  public boolean equals(final Object that)
  {
    return (that instanceof Occurrence) && (this.compareTo((Occurrence)that) == 0);
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(this.start, this.end);
  }

  @Override
  public int compareTo(final Occurrence that)
  {
    return ComparisonChain.start()
                          .compare(this.start, that.start)
                          .compare(this.end, that.end)
                          .result();
  }
}
