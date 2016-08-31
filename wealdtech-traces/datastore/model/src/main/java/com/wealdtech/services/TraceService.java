/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.wealdtech.Trace;
import com.wealdtech.WID;
import com.wealdtech.activities.Activity;
import com.wealdtech.contexts.Context;
import org.joda.time.LocalDateTime;

/**
 *
 */
public interface TraceService<T>  extends WObjectService<Trace, T>
{
  Trace obtain(WID<Trace> traceId);

  void remove(Trace trace);

  ImmutableList<Trace> obtain(Range<LocalDateTime> timeframe);

  ImmutableList<Trace> obtain(ImmutableSet<Context> contexts, ImmutableSet<Activity> activities, Range<LocalDateTime> timeframe);
}
