package test.com.wealdtech;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.DataError;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.utils.CompoundWID;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

public class CompoundWIDTest
{
  @Test
  public void testClass() throws Exception
  {
    final CompoundWID<Date, String> testWid1 = new CompoundWID<>(1, 2L);
    testWid1.toString();
    testWid1.hashCode();
    assertEquals(testWid1, testWid1);
    assertNotEquals(testWid1, null);
    assertNotEquals(null, testWid1);
    assertTrue(testWid1.compareTo(testWid1) == 0);

    final CompoundWID<Date, String> testWid2 = new CompoundWID<>(2, 3L);
    assertTrue(testWid2.compareTo(testWid1) != 0);
    assertTrue(testWid1.compareTo(testWid2) != 0);
    assertNotEquals(testWid1, testWid2);
  }

  @Test
  public void testFromString() throws Exception
  {
    final CompoundWID<Date, String> testWid = CompoundWID.fromString("7edcba09f7654321.2");
    testWid.toString();
    testWid.hashCode();
    assertEquals(testWid, testWid);
    assertNotEquals(testWid, null);
    assertNotEquals(null, testWid);

    // Ensure that NULL IDs don't work
    try
    {
      CompoundWID.<Date, String>fromString(null);
    }
    catch (DataError.Missing de)
    {
      // Good
    }

    // Ensure that invalid IDs don't work
    try
    {
      CompoundWID.<Date, String>fromString("invalid");
      fail("Managed to create a WID from an invalid string input");
    }
    catch (DataError.Bad de)
    {
      // Good
    }

    // Ensure that partial IDs don't work
    try
    {
      CompoundWID.<Date, String>fromString(".2");
      fail("Managed to create a WID from an invalid string input");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }

  @Test
  public void testSerialization1() throws Exception
  {
    final String input = "123456789abcdef.1b7d66a823c";
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final CompoundWID<Date, String> testWid = CompoundWID.fromString(input);
    final String output = mapper.writeValueAsString(testWid);
    assertEquals(output, "\"" + input + "\"");
  }

  @Test
  public void testSerialization2() throws Exception
  {
    final String input = "123456789abcdef";
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final CompoundWID<Date, String> testWid = CompoundWID.fromString(input);
    final String output = mapper.writeValueAsString(testWid);
    assertEquals(output, "\"" + input + "\"");
  }

  @Test
  public void testSerialization3() throws Exception
  {
    final String input = "123456789abcdef.";
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final CompoundWID<Date, String> testWid = CompoundWID.fromString(input);
    final String output = mapper.writeValueAsString(testWid);
    assertEquals(output, "\"123456789abcdef\"");
  }

  @Test
  public void testDeserialization() throws Exception
  {
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    final String input = "\"123456789abcdef.1b7d66a823c\"";
    final CompoundWID<Date, String> testWid = mapper.readValue(input, new TypeReference<CompoundWID<Date, String>>(){});
    assertEquals("\"" + testWid.toString() + "\"", input);
  }
}

