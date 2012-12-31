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
