package test.com.wealdtech.configuration;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.configuration.ConfigurationSource;

public class TestConfiguration
{
  @Test
  public void testConfiguration() throws Exception
  {
    SampleConfiguration tc = new ConfigurationSource<SampleConfiguration>().getConfiguration("config-test.json", SampleConfiguration.class);
    assertEquals(tc.getString(), "twenty three");
    assertEquals(tc.getInt(), 23);
    assertEquals(tc.getOptional(), "Default");
    assertEquals(tc.getSubConfiguration().getString(), "sub string");
  }

  @Test
  public void testConfiguration2() throws Exception
  {
    SampleConfiguration tc = new ConfigurationSource<SampleConfiguration>().getConfiguration("config-test2.json", SampleConfiguration.class);
    assertEquals(tc.getString(), "twenty three");
    assertEquals(tc.getInt(), 23);
    assertEquals(tc.getOptional(), "Another string");
    assertEquals(tc.getSubConfiguration().getString(), "sub string");
  }

  @Test
  public void testDefaultConfiguration() throws Exception
  {
    try
    {
      new ConfigurationSource<SampleConfiguration>().getConfiguration(SampleConfiguration.class);
    }
    catch (DataError de)
    {
      // Good
    }
  }

  @Test
  public void testMissingConfiguration() throws Exception
  {
    try
    {
      new ConfigurationSource<SampleConfiguration>().getConfiguration("missing-test.json", SampleConfiguration.class);
    }
    catch (DataError de)
    {
      // Good
    }
  }

  @Test
  public void testBadConfiguration() throws Exception
  {
    try
    {
      new ConfigurationSource<SampleConfiguration>().getConfiguration("bad-test.json", SampleConfiguration.class);
    }
    catch (DataError de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidConfiguration() throws Exception
  {
    try
    {
      new ConfigurationSource<SampleConfiguration>().getConfiguration("invalid-test.json", SampleConfiguration.class);
    }
    catch (DataError de)
    {
      // Good
    }
  }

  @Test
  public void testInvalidConfigurationUri() throws Exception
  {
    try
    {
      new ConfigurationSource<SampleConfiguration>().getConfiguration("file://invalid uri?bad = value!", SampleConfiguration.class);
    }
    catch (DataError de)
    {
      // Good
    }
  }

}
