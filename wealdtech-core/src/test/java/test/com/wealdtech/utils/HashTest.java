package test.com.wealdtech.utils;

import org.testng.annotations.Test;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.wealdtech.DataError;
import com.wealdtech.utils.Hash;
import com.wealdtech.utils.WealdMetrics;

import static org.testng.Assert.*;

public class HashTest
{
  @Test
  public void testHash() throws Exception
  {
    final String hashed = Hash.hash("Test");
    assertNotNull(hashed);
    assertTrue(Hash.matches("Test", hashed));
  }

  @Test
  public void testNullInput() throws Exception
  {
    try
    {
      Hash.hash(null);
      fail("Hashed NULL");
    }
    catch (DataError.Missing de)
    {
      // Good
    }

  }

  @Test
  public void testIsHashed() throws Exception
  {
    final String hashed = Hash.hash("Test");
    assertTrue(Hash.isHashed(hashed));
    assertFalse(Hash.isHashed("Test"));
    try
    {
      Hash.isHashed(null);
      fail("Considered NULL for hashed");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }

  @Test
  public void testNullMatch() throws Exception
  {
    final String hashed = Hash.hash("Test");
    assertFalse(Hash.matches(null, hashed));
  }

  @Test
  public void testNullHashed() throws Exception
  {
    try
    {
      assertFalse(Hash.matches("Test", null));
      fail("Attempted to match against NULL hashed value");
    }
    catch (DataError.Missing de)
    {
      // Good
    }
  }

  @Test
  public void testCache() throws Exception
  {
    // Ensure that the cache is operational
    final String fredHash = Hash.hash("Fred");
    final MetricRegistry registry = WealdMetrics.defaultRegistry();
    final Meter lookups = registry.getMeters().get(com.codahale.metrics.MetricRegistry.name(Hash.class, "lookups"));
    long initialLookupCount = lookups.getCount();
    final Meter misses = registry.getMeters().get(com.codahale.metrics.MetricRegistry.name(Hash.class, "misses"));
    long initialMissCount = misses.getCount();
    Hash.matches("Fred", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 1);
    assertEquals(misses.getCount() - initialMissCount, 1);
    Hash.matches("Fred", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 2);
    assertEquals(misses.getCount() - initialMissCount, 1);
    Hash.matches("Joe", fredHash);
    assertEquals(lookups.getCount() - initialLookupCount, 3);
    assertEquals(misses.getCount() - initialMissCount, 2);
  }
}
