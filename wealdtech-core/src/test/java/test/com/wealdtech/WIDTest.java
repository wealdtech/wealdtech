package test.com.wealdtech;

import com.wealdtech.DataError;
import com.wealdtech.WID;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

public class WIDTest
{
  @Test
  public void testClass()
  {
    final WID<Date> testWid1 = new WID<>(1);
    testWid1.toString();
    testWid1.hashCode();
    testWid1.getId();
    assertEquals(testWid1, testWid1);
    assertNotEquals(testWid1, null);
    assertNotEquals(null, testWid1);
    assertTrue(testWid1.compareTo(testWid1) == 0);

    final WID<Date> testWid2 = new WID<>(2);
    assertTrue(testWid2.compareTo(testWid1) != 0);
    assertTrue(testWid1.compareTo(testWid2) != 0);
    assertNotEquals(testWid1, testWid2);

    final WID<Date> testWid3 = new WID<>(2, 1L);
    testWid3.toString();
    testWid3.hashCode();
    testWid3.getId();
    assertEquals(testWid3, testWid3);
    assertNotEquals(testWid3, null);
    assertNotEquals(null, testWid3);
    assertEquals(testWid2.getId(), testWid3.getId());
    assertNotEquals(testWid2, testWid3);
    assertTrue(testWid1.compareTo(testWid3) < 0);
    assertTrue(testWid3.compareTo(testWid1) > 0);
    assertTrue(testWid2.compareTo(testWid3) < 0);
    assertTrue(testWid3.compareTo(testWid2) > 0);

    final WID<Date> testWid4 = new WID<>(2, 2L);
    assertTrue(testWid3.compareTo(testWid4) < 0);
    assertTrue(testWid4.compareTo(testWid3) > 0);
  }

  @Test
  public void testValidation()
  {
    // Test min and max shard ID
    WID.<Date>fromComponents(0, System.currentTimeMillis(), 0);
    try
    {
      WID.<Date>fromComponents(-1, System.currentTimeMillis(), 0);
    }
    catch (DataError.Bad de)
    {
      // Good
    }
    WID.<Date>fromComponents(WID.MAX_IID, System.currentTimeMillis(), 0);
    try
    {
      WID.<Date>fromComponents(WID.MAX_IID + 1, System.currentTimeMillis(), 0);
    }
    catch (DataError.Bad de)
    {
      // Good
    }

    // Test min and max timestamp
    WID.<Date>fromComponents(0, WID.EPOCH, 0);
    try
    {
      WID.<Date>fromComponents(0, WID.EPOCH - 1, 0);
    }
    catch (DataError.Bad de)
    {
      // Good
    }
    WID.<Date>fromComponents(0, WID.MAX_TIMESTAMP, 0);
    try
    {
      WID.<Date>fromComponents(0, WID.MAX_TIMESTAMP + 1, 0);
    }
    catch (DataError.Bad de)
    {
      // Good
    }

    // Test min and max ID
    WID.<Date>fromComponents(0, System.currentTimeMillis(), 0);
    try
    {
      WID.<Date>fromComponents(0, System.currentTimeMillis(), -1);
    }
    catch (DataError.Bad de)
    {
      // Good
    }
    try
    {
      WID.<Date>fromComponents(0, System.currentTimeMillis(), WID.MAX_IID + 1);
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testFromString()
  {
    final WID<Date> testWid = WID.fromString("7edcba09f7654321");
    testWid.toString();
    testWid.hashCode();
    assertEquals(testWid, testWid);
    assertNotEquals(testWid, null);
    assertNotEquals(null, testWid);
    assertEquals(testWid.getShardId(), 4058);
    assertEquals(testWid.getTimestamp(), 1525133781328L);
    assertEquals(testWid.getIid(), 801);

    final WID<Date> testWid2 = WID.fromString("7edcba09f7654321.1");
    assertNotEquals(testWid, testWid2);
    assertTrue(testWid2.hasSubId());
    assertEquals(testWid2.getSubId().get(), (Long)1L);
    assertEquals(testWid.getId(), testWid2.getId());
  }

  @Test(expectedExceptions = {DataError.Missing.class})
  public void testInvalidFromString1()
  {
    WID.<Date>fromString(null);
  }

  @Test(expectedExceptions = {DataError.Bad.class})
  public void testInvalidFromString2()
  {
    WID.<Date>fromString("invalid");
  }

  @Test(expectedExceptions = {DataError.Bad.class})
  public void testInvalidFromString3()
  {
    WID.<Date>fromString("7edcba09f7654321.");
  }

  @Test(expectedExceptions = {DataError.Bad.class})
  public void testInvalidFromString4()
  {
    WID.<Date>fromString(".1");
  }

  @Test
  public void testTimestamp()
  {
    final WID<Date> testWid = WID.fromString("0123456789abcdef");
    final long timestamp = testWid.getTimestamp();
    assertEquals(timestamp, 2224532175603L);
  }

  @Test
  public void testFromComponents()
  {
    final long timestamp = 1358784686000L;
    final long shardId = 3824L;
    final long id = 952L;
    final WID<Date> testWid = WID.fromComponents(shardId, timestamp, id);
    assertEquals(testWid.getTimestamp(), timestamp);
    assertEquals(testWid.getShardId(), shardId);
  }

  @Test
  public void testRandomWID()
  {
    for (int i = 0; i < 1000000; i++)
    {
      WID.<Date>randomWID();
      WID.<Date>randomWIDWithSubId();
    }
  }

  @Test
  public void testFromLong()
  {
    WID<String> testWid1 = WID.fromLong(12345678901234L);

    WID<String> testWid2 = WID.fromLongs(12345678901234L, 1L);

    WID<String> testWid3 = WID.fromLongs(12345678901234L, null);
  }

  @Test(expectedExceptions = {DataError.Missing.class})
  public void testInvalidFromLong1()
  {
    WID.<String>fromLong(null);
  }

  @Test
  public void testRecast()
  {
    final WID<Date> testWid1 = WID.fromLongs(12345678901234L, 1L);
    final WID<String> testWid2 = WID.recast(testWid1);
    // Note that this succeeds, as we do not retain the type of the WID
    assertEquals(testWid1, testWid2);
  }

  @Test
  public void testWithSubId1()
  {
    final WID<Date> testWid1 = WID.fromLongs(12345678901234L, 1L);
    final WID<Date> testWid2 = testWid1.withSubId(2L);
    assertTrue(testWid2.hasSubId());
    assertEquals(testWid2.getSubId().get(), (Long)2L);
    final WID<Date> testWid3 = testWid1.withSubId(null);
    assertFalse(testWid3.hasSubId());
  }
}

