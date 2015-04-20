/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

/**
 * A rationale is a set of reasons for doing something.  Rationales are used within Wealdtech code to be able to provide structured
 * information about why an object is in the state that it is in.
 */
public class Rationale extends WObject<Rationale> implements Comparable<Rationale>
{
  @JsonCreator
  public Rationale(final Map<String, Object> data)
  {
    super(data);
  }
}
