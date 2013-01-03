/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package test.com.wealdtech.utils;

import org.testng.annotations.Test;

import com.wealdtech.utils.StringUtils;

import static org.testng.Assert.assertEquals;

public class TestStringUtils
{
  @Test
  public void testRandomGeneration() throws Exception
  {
    String random1 = StringUtils.generateRandomString(6);
    assertEquals(random1.length(), 6);
  }

  @Test
  public void testCapitalize() throws Exception
  {
    final String teststring = "teststring";
    final String teststring2 = "NoChangeString";

    assertEquals("Teststring", StringUtils.capitalize(teststring));
    assertEquals(teststring2, StringUtils.capitalize(teststring2));
  }

  @Test
  public void testNameToGetter() throws Exception
  {
    final String testvar = "test";
    final String testvar2 = "Capital";

    assertEquals("getTest", StringUtils.nameToGetter(testvar));
    assertEquals("getCapital", StringUtils.nameToGetter(testvar2));
  }
}
