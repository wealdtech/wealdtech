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
 * Information on the failure of a data provider
 */
public enum DataProviderFailure
{
  /**
   * The transport is unavailable, for example if the network is unavailable
   */
  TRANSPORT,
  /**
   * The service is unavailable, for example if the target webserver is down
   */
  SERVICE,
  /**
   * The access is unauthorised, for example if an API key is invalid
   */
  UNAUTHORISED,
  /**
   * The parameters are invalid, for example if a mandatory parameter is missing
   */
  PARAMETERS,
  /**
   * Generic failure
   */
  FAILED;
}
