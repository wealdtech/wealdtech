package test.com.wealdtech.utils;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.utils.Hash;

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
}
