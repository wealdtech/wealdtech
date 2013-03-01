package test.com.wealdtech;

import org.testng.annotations.Test;

import com.wealdtech.TwoTuple;

import static org.testng.Assert.*;

public class TwoTupleTest
{
  @Test
  public void testClass() throws Exception
  {
    final TwoTuple<String, Integer> testTwoTuple1 = new TwoTuple<>("Test", 1);
    testTwoTuple1.getS();
    testTwoTuple1.getT();
    testTwoTuple1.toString();
    testTwoTuple1.hashCode();
    assertEquals(testTwoTuple1, testTwoTuple1);
    assertNotEquals(testTwoTuple1, null);
    assertNotEquals(null, testTwoTuple1);
    assertFalse(testTwoTuple1.equals("test"));

    final TwoTuple<String, Integer> testTwoTuple2 = new TwoTuple<>("Another test", 2);
    testTwoTuple2.getS();
    testTwoTuple2.getT();
    assertNotEquals(testTwoTuple1, testTwoTuple2);

    final TwoTuple<String, Integer> testTwoTuple3 = new TwoTuple<>(null, null);
    testTwoTuple3.getS();
    testTwoTuple3.getT();
    testTwoTuple1.toString();
    testTwoTuple1.hashCode();
    assertEquals(testTwoTuple3, testTwoTuple3);
    assertNotEquals(testTwoTuple1, testTwoTuple3);

    final TwoTuple<String, Integer> testTwoTuple4 = new TwoTuple<>("Test", 2);
    assertNotEquals(testTwoTuple1, testTwoTuple4);
  }

//  @Test
//  public void testValidation() throws Exception
//  {
//    // Test min and max shard ID
//    WID.<Date>fromComponents(0, System.currentTimeMillis(), 0);
//    try
//    {
//      WID.<Date>fromComponents(-1, System.currentTimeMillis(), 0);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//    try
//    {
//      WID.<Date>fromComponents(WID.MAX_IID + 1, System.currentTimeMillis(), 0);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//
//    // Test min and max timestamp
//    WID.<Date>fromComponents(0, WID.EPOCH, 0);
//    try
//    {
//      WID.<Date>fromComponents(0, WID.EPOCH - 1, 0);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//    try
//    {
//      WID.<Date>fromComponents(0, WID.MAX_TIMESTAMP + 1, 0);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//
//    // Test min and max ID
//    WID.<Date>fromComponents(0, System.currentTimeMillis(), 0);
//    try
//    {
//      WID.<Date>fromComponents(0, System.currentTimeMillis(), -1);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//    try
//    {
//      WID.<Date>fromComponents(0, System.currentTimeMillis(), WID.MAX_IID + 1);
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//  }
//
//  @Test
//  public void testFromString() throws Exception
//  {
//    final WID<Date> testWid = WID.<Date>fromString("7edcba09f7654321");
//    testWid.toString();
//    testWid.hashCode();
//    assertEquals(testWid, testWid);
//    assertNotEquals(testWid, null);
//    assertNotEquals(null, testWid);
//    assertEquals(testWid.getShardId(), 4058);
//    assertEquals(testWid.getTimestamp(), 1525133781328L);
//    assertEquals(testWid.getIid(), 801);
//
//    // Ensure that NULL IDs don't work
//    try
//    {
//      WID.<Date>fromString(null);
//    }
//    catch (DataError.Missing de)
//    {
//      // Good
//    }
//
//    // Ensure that invalid IDs don't work
//    try
//    {
//      WID.<Date>fromString("invalid");
//    }
//    catch (DataError.Bad de)
//    {
//      // Good
//    }
//  }
//
//  @Test
//  public void testTimestamp() throws Exception
//  {
//    final WID<Date> testWid = WID.<Date>fromString("0123456789abcdef");
//    final long timestamp = testWid.getTimestamp();
//    // TODO test
//  }
//
//  @Test
//  public void testFromComponents() throws Exception
//  {
//    final long timestamp = 1358784686000L;
//    final long shardId = 3824L;
//    final long id = 952L;
//    final WID<Date> testWid = WID.<Date>fromComponents(shardId, timestamp, id);
//    assertEquals(testWid.getTimestamp(), timestamp);
//    assertEquals(testWid.getShardId(), shardId);
//  }
//
//  @Test
//  public void testSerialization() throws Exception
//  {
//    final String input = "123456789abcdef";
//    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
//    final WID<Date> testWid = WID.<Date>fromString(input);
//    final String output = mapper.writeValueAsString(testWid);
//    assertEquals(output, "\"" + input + "\"");
//  }
//
//  @Test
//  public void testDeserialization() throws Exception
//  {
//    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
//    final String input = "\"123456789abcdef\"";
//    final WID<Date> testWid = mapper.readValue(input, new TypeReference<WID<Date>>(){});
//    assertEquals("\"" + testWid.toString() + "\"", input);
//  }
//
//  @Test
//  public void testRandomWID() throws Exception
//  {
//    for (int i = 0; i < 1000000; i++)
//    {
//      WID.<Date>randomWID();
//    }
//  }
}

