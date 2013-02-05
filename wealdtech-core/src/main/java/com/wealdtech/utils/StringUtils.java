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

package com.wealdtech.utils;

import java.security.SecureRandom;

public enum StringUtils
{
  INSTANCE;

  private static final SecureRandom RANDOMSOURCE;
  private static final String CANDIDATES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  static
  {
    RANDOMSOURCE = new SecureRandom();
  }

  /**
   * Generate a random string of alphanumeric characters.
   * <p>
   * The string returned will contain characters randomly
   * selected from upper- and lower-case a through z as
   * well as the digits 0 through 9.
   * @param length the length of the string to generate
   * @return a string of random alphanumeric characters of the requested length
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

  /**
   * Capitalize a string.
   * Useful for handling introspection and camel case.
   * @param str the string to capitalize
   * @return The capitalized string
   */
  public static String capitalize(final String str)
  {
    final StringBuilder sb = new StringBuilder(str);
    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
    return sb.toString();
  }

  /**
   * Change a variable name to the name of the getter.
   * @param str a variable name in camelcase
   * @return The name of the getter
   */
  public static String nameToGetter(final String str)
  {
    final StringBuilder sb = new StringBuilder("get");
    sb.append(str);
    sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
    return sb.toString();
  }
}
