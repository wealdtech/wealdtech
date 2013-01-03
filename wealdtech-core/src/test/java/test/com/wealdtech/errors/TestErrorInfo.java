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

package test.com.wealdtech.errors;

import org.testng.annotations.Test;

import com.wealdtech.errors.ErrorInfo;

import static org.testng.Assert.*;

public class TestErrorInfo
{
  @Test
  public void testErrorInfo() throws Exception
  {
    ErrorInfo test1 = new ErrorInfo("code1",
                                    "user message",
                                    "developer message",
                                    new java.net.URI("http://www.example.com/test"));
    test1.toString();
    test1.hashCode();
    assertEquals(test1, test1);
    assertNotEquals(null, test1);
    assertEquals(test1.getErrorCode(), "code1");
    assertEquals(test1.getUserMessage(), "user message");
    assertEquals(test1.getDeveloperMessage(), "developer message");
    assertEquals(test1.getMoreInfo().toString(), "http://www.example.com/test");

    ErrorInfo test2 = new ErrorInfo("code2",
                                    null,
                                    null,
                                    null);
    test2.toString();
    test2.hashCode();
    assertEquals(test2, test2);
    assertNotEquals(null, test2);
    assertNotEquals(test2, test1);
    assertEquals(test2.getErrorCode(), "code2");
    assertEquals(test2.getUserMessage(), null);
    assertEquals(test2.getDeveloperMessage(), null);
    assertEquals(test2.getMoreInfo(), null);
  }
}
