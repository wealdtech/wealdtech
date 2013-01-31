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

/**
 * Utilities for hashing data (mainly passwords)
 */
public enum Hash
{
  INSTANCE;
  private static final Logger LOGGER = LoggerFactory.getLogger(Hash.class);

  // Cache of (input, salt), match
  private static final LoadingCache<TwoTuple<String, String>, Boolean> cache;

  // Metrics
  private static final Meter cacheMisses;
  private static final Timer gets;

  static
  {
    cacheMisses = Metrics.defaultRegistry().newMeter(Hash.class, "cache-misses", "lookups", TimeUnit.SECONDS);
    gets = Metrics.defaultRegistry().newTimer(Hash.class, "gets", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    final CacheBuilder<Object, Object> cb = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(2,  TimeUnit.MINUTES);
    cache = cb.recordStats().build(new CacheLoader<TwoTuple<String, String>, Boolean>()
    {
      @Override
      public Boolean load(final TwoTuple<String, String> input)
      {
        cacheMisses.mark();
        return calculateMatches(input.getS(), input.getT());
      }
    });
  }

  // Hash an input
  public static String hash(final String input)
  {
    return BCrypt.hashpw(input, BCrypt.gensalt(12));
  }

  /**
   * Check to see if a plaintext input matches a hash
   * @param input the input
   * @param hashed the hash
   * @return <code>true</code> if it matches, <code>false</code> if not
   */
  public static boolean matches(final String input, final String hashed)
  {
    final TimerContext context = gets.time();
    try
    {
      boolean result = false;
      try
      {
        result = cache.get(new TwoTuple<String, String>(input, hashed));
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
