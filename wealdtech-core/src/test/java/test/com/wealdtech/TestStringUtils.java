package test.com.wealdtech;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.wealdtech.utils.StringUtils;

public class TestStringUtils
{
  @Test
  public void testRandomGeneration() throws Exception
  {
    String random1 = StringUtils.generateRandomString(6);
    assertEquals(random1.length(), 6);
  }
}
