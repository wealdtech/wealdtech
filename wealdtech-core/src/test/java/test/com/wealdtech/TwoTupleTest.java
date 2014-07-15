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
}

