/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto;

/**
 * Information on the current state of a data provider
 */
public enum DataProviderState
{
  /**
   * The provider is not providing data
   */
  NOT_PROVIDING,
  /**
   * The provider wants to provide data but is not configured
   */
  AWAITING_CONFIGURATION,
  /**
   * The provider is providing data
   */
  PROVIDING,
  /**
   * The provider is providing in a degraded fashion
   */
  DEGRADED,
  /**
   * The provider has failed
   */
  FAILED;
}
