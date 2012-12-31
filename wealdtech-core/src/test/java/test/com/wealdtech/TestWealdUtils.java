package test.com.wealdtech;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.wealdtech.WealdUtils;

public class TestWealdUtils
{
  @Test
  public void testRandomGeneration() throws Exception
  {
    String random1 = WealdUtils.generateRandomString(6);
    assertEquals(random1.length(), 6);
  }
}
