package test.com.wealdtech;
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.ServerError;

public class TestExceptions
{
  @Test
  public void testDataError() throws Exception
  {
    try
    {
      throw new DataError();
    }
    catch (DataError de)
    {
      // Good
    }

    try
    {
      throw new DataError("Test data error 2");
    }
    catch (DataError de)
    {
      assertEquals(de.getMessage(), "Test data error 2");
      try
      {
        throw new DataError("Test data error 3", de);
      }
      catch (DataError de2)
      {
        assertEquals(de2.getMessage(), "Test data error 3");
      }
    }
  }

  @Test
  public void testServerError() throws Exception
  {
    try
    {
      throw new ServerError();
    }
    catch (ServerError de)
    {
      // Good
    }

    try
    {
      throw new ServerError("Test server error 2");
    }
    catch (ServerError de)
    {
      assertEquals(de.getMessage(), "Test server error 2");
      try
      {
        throw new ServerError("Test server error 3", de);
      }
      catch (ServerError de2)
      {
        assertEquals(de2.getMessage(), "Test server error 3");
      }
    }
  }
}
