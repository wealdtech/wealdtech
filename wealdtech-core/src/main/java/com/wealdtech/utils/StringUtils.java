/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import javax.annotation.Nullable;
import java.security.SecureRandom;

public enum StringUtils
{
  INSTANCE;

  private static final SecureRandom RANDOMSOURCE;
  private static final String CANDIDATES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int CANDIDATESLEN;
  private static final String GET = "get";
  private static final int GETLEN;

  static
  {
    RANDOMSOURCE = new SecureRandom();
    CANDIDATESLEN = CANDIDATES.length();
    GETLEN = GET.length();
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
      sb.append(CANDIDATES.charAt(RANDOMSOURCE.nextInt(CANDIDATESLEN)));
    }
    return sb.toString();
  }

  /**
   * Capitalize a string.
   * Useful for handling introspection and camel case.
   * @param str the string to capitalize
   * @return The capitalized string
   */
  @Nullable
  public static String capitalize(@Nullable final String str)
  {
    if (str == null)
    {
      return null;
    }
    final StringBuilder sb = new StringBuilder(str);
    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
    return sb.toString();
  }

  /**
   * Change a variable name to the name of the getter.
   * @param str a variable name in camelcase
   * @return The name of the getter
   */
  @Nullable
  public static String nameToGetter(@Nullable final String str)
  {
    if (str == null)
    {
      return null;
    }
    final StringBuilder sb = new StringBuilder(GET);
    sb.append(str);
    sb.setCharAt(GETLEN, Character.toUpperCase(sb.charAt(GETLEN)));
    return sb.toString();
  }

  /**
   * Return the substring from the start of the string up to, but not including, the provided separator
   * @param str the string
   * @param sep the separator
   * @return the substring
   */
  @Nullable
  public static String substringBefore(@Nullable final String str, @Nullable final String sep)
  {
    if (str == null)
    {
      return null;
    }
    if (sep == null)
    {
      return str;
    }
    final int index = str.indexOf(sep);
    if (index == -1)
    {
      return str;
    }
    return str.substring(0, index);
  }
}
