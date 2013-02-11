package test.com.wealdtech.errors;

import java.net.URI;

import org.testng.annotations.Test;

import com.wealdtech.DataError;
import com.wealdtech.errors.ErrorInfo;

import static org.testng.Assert.*;

public class ErrorInfoTest
{
  @Test
  public void testModel() throws Exception
  {
    final ErrorInfo testErrorInfo1 = new ErrorInfo("Error code", "User message", "Developer message", new URI("http://errors.wealdtech.com/error"));
    testErrorInfo1.toString();
    testErrorInfo1.hashCode();
    testErrorInfo1.getErrorCode();
    testErrorInfo1.getUserMessage();
    testErrorInfo1.getDeveloperMessage();
    testErrorInfo1.getMoreInfo();
    assertEquals(testErrorInfo1, testErrorInfo1);
    assertNotEquals(null, testErrorInfo1);
    assertNotEquals(testErrorInfo1, null);
    assertFalse(testErrorInfo1.equals("Test"));
    assertFalse(testErrorInfo1.equals(null));
    assertTrue(testErrorInfo1.equals(testErrorInfo1));

    final ErrorInfo testErrorInfo2 = new ErrorInfo("Error code", "User message", "Developer message", "http://errors.wealdtech.com/error");
    assertEquals(testErrorInfo1, testErrorInfo2);

    final ErrorInfo testErrorInfo3 = new ErrorInfo(null, null, null, (String)null);
    testErrorInfo3.toString();
    testErrorInfo3.hashCode();
    testErrorInfo3.getErrorCode();
    testErrorInfo3.getUserMessage();
    testErrorInfo3.getDeveloperMessage();
    testErrorInfo3.getMoreInfo();
    assertEquals(testErrorInfo3, testErrorInfo3);
    assertNotEquals(null, testErrorInfo3);
    assertNotEquals(testErrorInfo3, null);

  }

  @Test
  public void testValidation() throws Exception
  {
    try
    {
      new ErrorInfo("Error code", "User message", "Developer message", "http://1.2.3.4.5.6.[7]/:-/uri?bad?bad?bad");
      fail("Created error info with invalid URI");
    }
    catch (DataError.Bad de)
    {
      // Good
    }
  }
}
