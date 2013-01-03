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
package com.wealdtech.errors;

import java.util.Map;

import com.google.common.collect.MapMaker;

/**
 * A static map holding information on errors.
 * <p/>Additional errors should be placed in this map to allow
 * for global shared access of error information.
 */
public class ErrorInfoMap
{
  private static transient final Map<String, ErrorInfo> map;
  static
  {
    map = new MapMaker().concurrencyLevel(1).makeMap();
  }

  /**
   * Obtain information about an error given its error code.
   * @param errorCode the error code
   * @return the error info; <code>null</code> if the error is unknown
   */
  public static ErrorInfo get(final String errorCode)
  {
    return map.get(errorCode);
  }

  /**
   * Add information about an error code.
   * @param info the error information
   */
  public static synchronized void put(final ErrorInfo info)
  {
    map.put(info.getErrorCode(), info);
  }
}
