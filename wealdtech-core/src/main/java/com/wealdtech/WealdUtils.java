/*
 *    Copyright 2012 Weald Technology Trading Limited
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

package com.wealdtech;

import java.security.SecureRandom;

public class WealdUtils
{
  private static final SecureRandom RANDOMSOURCE;
  private static String CANDIDATES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvexyz0123456789";

  static
  {
    RANDOMSOURCE = new SecureRandom();
  }

  /**
   * Generate a random string of alphanumeric characters
   */
  public static String generateRandomString(int length)
  {
    final StringBuffer sb = new StringBuffer(length);
    for (int i = 0; i < length; i++)
    {
      sb.append(CANDIDATES.charAt(RANDOMSOURCE.nextInt(62)));
    }
    return sb.toString();
  }
}
