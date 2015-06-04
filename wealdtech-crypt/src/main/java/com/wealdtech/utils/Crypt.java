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

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 */
public enum Crypt
{
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(Crypt.class);

  private static final HashConfiguration CONFIGURATION;

  static
  {
    // TODO where to obtain this from?
    CONFIGURATION = new HashConfiguration();
  }

  /**
   * Hash an input string using a random salt.
   * @param input the input string
   * @return The hash of the input string.
   */
  public static String hash(final String input)
  {
    checkNotNull(input, "Cannot hash NULL");
    return BCrypt.hashpw(input, BCrypt.gensalt(CONFIGURATION.getStrength()));
  }

  /**
   * Check to see if a plaintext input matches a hash
   * @param input the input
   * @param hashed the hash
   * @return <code>true</code> if it matches, <code>false</code> if not
   */
  public static boolean matches(final String input, final String hashed)
  {
    checkNotNull(hashed, "Cannot compare NULL");
    Boolean result = false;
    try
    {
      result = BCrypt.checkpw(input, hashed);
    }
    catch (IllegalArgumentException iae)
    {
      LOG.error("Failed to calculate hash for input password", iae);
    }
    return result;
  }

  public static boolean isHashed(final String input)
  {
    checkNotNull(input, "Cannot consider NULL");

    return input.startsWith("$2a$") || input.startsWith("$2$");
  }
}
