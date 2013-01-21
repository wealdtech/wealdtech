package test.com.wealdtech;

import java.util.Date;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.WID;
import com.wealdtech.jackson.ObjectMapperFactory;

import static org.testng.Assert.*;

public class WIDTest
{
  @Test
  public void testClass() throws Exception
  {
    final WID<Date> testWid = new WID<>(1);
    testWid.toString();
    testWid.hashCode();
    assertEquals(testWid, testWid);
    assertNotEquals(testWid, null);
    assertNotEquals(null, testWid);
  }

  @Test
  public void testFromString() throws Exception
  {
    final WID<Date> testWid = WID.<Date>fromString("0123456789abcdef");
    testWid.toString();
    testWid.hashCode();
    assertEquals(testWid, testWid);
    assertNotEquals(testWid, null);
    assertNotEquals(null, testWid);
    // TODO add other tests
    assertEquals(testWid.getShardId(), 2803);
  }

  @Test
  public void testTimestamp() throws Exception
  {
    final WID<Date> testWid = WID.<Date>fromString("0123456789abcdef");
    final long timestamp = testWid.getTimestamp();
    // TODO test
  }

  @Test
  public void testFromComponents() throws Exception
  {
    final long timestamp = 1358784686000L;
    final long shardId = 7134L;
    final long id = 952L;
    final WID<Date> testWid = WID.<Date>fromComponents(timestamp, shardId, id);
    System.err.println(testWid);
    assertEquals(testWid.getTimestamp(), timestamp);
    assertEquals(testWid.getShardId(), shardId);
  }

  @Test
  public void testSerialization() throws Exception
  {
    final String input = "123456789abcdef";
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final WID<Date> testWid = WID.<Date>fromString(input);
    final String output = mapper.writeValueAsString(testWid);
    assertEquals(output, "\"" + input + "\"");
  }

  @Test
  public void testDeserialization() throws Exception
  {
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final String input = "\"123456789abcdef\"";
    final WID<Date> testWid = mapper.readValue(input, new TypeReference<WID<Date>>(){});
    assertEquals("\"" + testWid.toString() + "\"", input);
  }
}

