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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wealdtech.errors.ErrorInfo;
import com.wealdtech.errors.ErrorInfoMap;

import static org.testng.Assert.*;

public class TestErrorInfoMap
{
  ErrorInfo test1, test2;
  @BeforeClass
  public void setUp() throws Exception
  {
    this.test1 = new ErrorInfo("code1",
                               "user message",
                               "developer message",
                               new java.net.URI("http://www.example.com/test"));

    this.test2 = new ErrorInfo("code2",
                               "another user message",
                               "another developer message",
                               new java.net.URI("http://www.example.com/anothertest"));
  }

  @Test
  public void testErrorInfoMap() throws Exception
  {
    assertNull(ErrorInfoMap.get(this.test1.getErrorCode()));
    ErrorInfoMap.put(this.test1);
    assertNotNull(ErrorInfoMap.get(this.test1.getErrorCode()));

    assertNull(ErrorInfoMap.get(this.test2.getErrorCode()));
    ErrorInfoMap.put(this.test2);
    assertNotNull(ErrorInfoMap.get(this.test2.getErrorCode()));
  }
}
