package test.com.wealdtech.utils;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.utils.CacheConfiguration;
import com.wealdtech.utils.HashConfiguration;

import static org.testng.Assert.*;

public class HashConfigurationTest
{
  @Test
  public void testHashConfiguration() throws Exception
  {
    HashConfiguration testHashConfiguration1 = new HashConfiguration.Builder()
                                                                    .strength(10)
                                                                    .cacheConfiguration(new CacheConfiguration())
                                                                    .build();

    HashConfiguration testHashConfiguration2 = new HashConfiguration.Builder(testHashConfiguration1)
                                                                    .strength(5)
                                                                    .cacheConfiguration(new CacheConfiguration())
                                                                    .build();

    assertNotEquals(testHashConfiguration1, testHashConfiguration2);
  }

  @Test
  public void testInvalidHashConfiguration() throws Exception
  {
    try
    {
      new HashConfiguration.Builder().strength(2).build();
      fail("Created hash configuration with invalid strength");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }
}
