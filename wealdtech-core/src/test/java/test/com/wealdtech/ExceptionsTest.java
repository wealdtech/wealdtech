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

package test.com.wealdtech;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.ServerError;

import static org.testng.Assert.*;

public class ExceptionsTest
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
      de.toString();
      de.getUserMessage();
      de.getUrl();
      de.getThrowingClassName();
      de.getThrowingMethodName();
    }

    try
    {
      try
      {
        throw new ServerError("Test");
      }
      catch (ServerError se)
      {
        se.toString();
        throw new DataError(se);
      }
    }
    catch (DataError se)
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

    try
    {
      throw new DataError.Authentication("Bad");
    }
    catch (DataError de)
    {
      // Good
    }
    try
    {
      try
      {
        throw new ServerError("Bad");
      }
      catch (ServerError se)
      {
        throw new DataError.Authentication("Bad", se);
      }
    }
    catch (DataError de)
    {
      // Good
    }

    try
    {
      throw new DataError.Permission("Bad");
    }
    catch (DataError de)
    {
      // Good
    }
    try
    {
      try
      {
        throw new ServerError("Bad");
      }
      catch (ServerError se)
      {
        throw new DataError.Permission("Bad", se);
      }
    }
    catch (DataError de)
    {
      // Good
    }

    try
    {
      throw new DataError.Missing("Bad");
    }
    catch (DataError de)
    {
      // Good
    }
    try
    {
      try
      {
        throw new ServerError("Bad");
      }
      catch (ServerError se)
      {
        throw new DataError.Missing("Bad", se);
      }
    }
    catch (DataError de)
    {
      // Good
    }

    try
    {
      throw new DataError.Bad("Bad");
    }
    catch (DataError de)
    {
      // Good
    }
    try
    {
      try
      {
        throw new ServerError("Bad");
      }
      catch (ServerError se)
      {
        throw new DataError.Bad("Bad", se);
      }
    }
    catch (DataError de)
    {
      // Good
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
      try
      {
        throw new DataError.Missing("Test");
      }
      catch (DataError de)
      {
        throw new ServerError(de);
      }
    }
    catch (ServerError se)
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
