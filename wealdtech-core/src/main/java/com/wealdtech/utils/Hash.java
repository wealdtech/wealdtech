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
package com.wealdtech.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wealdtech.TwoTuple;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import static com.wealdtech.Preconditions.*;

/**
 * Utilities for hashing data using the Bcrypt algorithm.
 * <p/>Due to the expense of hashing using bcrypt this class
 * uses a short-term cache to reduce the cost of recalculating the same
 * hash multiple times.  As such, this class should only be used on secure
 * systems.
 */
public enum Hash
{
  INSTANCE;
  private static final Logger LOGGER = LoggerFactory.getLogger(Hash.class);

  // Cache of (input, salt), match
  private static final LoadingCache<TwoTuple<String, String>, Boolean> CACHE;

  // Metrics
  private static final Meter CACHEMISSES;
  private static final Timer GETS;

  private static final HashConfiguration CONFIGURATION;

  static
  {
    // TODO where to obtain this from?
    CONFIGURATION = new HashConfiguration();

    CACHEMISSES = Metrics.defaultRegistry().newMeter(Hash.class, "cache-misses", "lookups", TimeUnit.SECONDS);
    GETS = Metrics.defaultRegistry().newTimer(Hash.class, "gets", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    final CacheBuilder<Object, Object> cb = CacheBuilder.newBuilder()
                                                        .maximumSize(CONFIGURATION.getCacheConfiguration().getMaxEntries())
                                                        .expireAfterWrite(CONFIGURATION.getCacheConfiguration().getMaxDuration(),  TimeUnit.SECONDS);
    CACHE = cb.recordStats().build(new CacheLoader<TwoTuple<String, String>, Boolean>()
    {
      @Override
      public Boolean load(final TwoTuple<String, String> input)
      {
        CACHEMISSES.mark();
        return calculateMatches(input.getS(), input.getT());
      }
    });
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
    final TimerContext context = GETS.time();
    try
    {
      boolean result = false;
      try
      {
        result = CACHE.get(new TwoTuple<String, String>(input, hashed));
      }
      catch (ExecutionException e)
      {
        LOGGER.error("Failed to hash input password", e);
      }
      return result;
    }
    finally
    {
      context.stop();
    }
  }

  // Calculate a hash given an input and hashed version
  private static boolean calculateMatches(final String input, final String hashed)
  {
    Boolean result = false;
    try
    {
      result = BCrypt.checkpw(input, hashed);
    }
    catch (IllegalArgumentException iae)
    {
      LOGGER.error("Failed to calculate hash for input password", iae);
    }
    return result;
  }

  public static boolean isHashed(final String input)
  {
    checkNotNull(input, "Cannot consider NULL");

    boolean hashed;
    if (input.startsWith("$2a$") || input.startsWith("$2$"))
    {
      hashed = true;
    }
    else
    {
      hashed = false;
    }
    return hashed;
  }
}
