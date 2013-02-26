package test.com.wealdtech.utils;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.utils.CacheConfiguration;

import static org.testng.Assert.*;

public class CacheConfigurationTest
{
  @Test
  public void testCacheConfiguration() throws Exception
  {
    CacheConfiguration testCacheConfiguration1 = new CacheConfiguration.Builder()
                                                                       .build();

    CacheConfiguration testCacheConfiguration2 = new CacheConfiguration.Builder(testCacheConfiguration1)
                                                                       .maxEntries(100)
                                                                       .maxDuration(600)
                                                                       .build();

    assertNotEquals(testCacheConfiguration1, testCacheConfiguration2);
  }

  @Test
  public void testInvalidCacheConfiguration() throws Exception
  {
    try
    {
      new CacheConfiguration.Builder().maxEntries(1000001).build();
      fail("Created cache configuration with invalid maximum entries");
    }
    catch (DataError.Bad de)
    {
      // Good
    }

    try
    {
      new CacheConfiguration.Builder().maxDuration(1000001).build();
      fail("Created cache configuration with invalid maximum duration");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }
}
