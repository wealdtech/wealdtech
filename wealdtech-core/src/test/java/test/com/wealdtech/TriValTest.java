package test.com.wealdtech;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ComparisonChain;
import com.wealdtech.TriVal;
import com.wealdtech.utils.TriValOrdering;

public class TriValTest
{
  @Test
  public void testComparisonChain() throws Exception
  {
    // Tests with Guava's comparisonchain
    final TriVal<String> triAbsent = TriVal.<String>absent();
    final TriVal<String> triClear = TriVal.<String>clear();
    final TriVal<String> triTest1 = TriVal.<String>of("Test 1");
    final TriVal<String> triTest2 = TriVal.<String>of("Test 2");
    final TriVal<String> triTest1Again = TriVal.<String>of("Test 1");

    assertEquals(ComparisonChain.start().compare(triAbsent, triAbsent, new TriValOrdering<String>()).result(), 0);
    assertEquals(ComparisonChain.start().compare(triAbsent, triClear, new TriValOrdering<String>()).result(), -1);
    assertEquals(ComparisonChain.start().compare(triAbsent, triTest1, new TriValOrdering<String>()).result(), -1);
    assertEquals(ComparisonChain.start().compare(triClear, triAbsent, new TriValOrdering<String>()).result(), 1);
    assertEquals(ComparisonChain.start().compare(triClear, triClear, new TriValOrdering<String>()).result(), 0);
    assertEquals(ComparisonChain.start().compare(triClear, triTest1, new TriValOrdering<String>()).result(), -1);
    assertEquals(ComparisonChain.start().compare(triTest1, triAbsent, new TriValOrdering<String>()).result(), 1);
    assertEquals(ComparisonChain.start().compare(triTest1, triClear, new TriValOrdering<String>()).result(), 1);
    assertEquals(ComparisonChain.start().compare(triTest1, triTest1, new TriValOrdering<String>()).result(), 0);
    assertEquals(ComparisonChain.start().compare(triTest1, triTest1Again, new TriValOrdering<String>()).result(), 0);
    assertEquals(ComparisonChain.start().compare(triTest2, triTest1Again, new TriValOrdering<String>()).result(), 1);
  }
}
