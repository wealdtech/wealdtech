package test.com.wealdtech.jersey;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.jersey.guice.JerseyServletModule;
import com.wealdtech.jetty.JettyServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class JerseyTest
{
  private URI validuri1;
  private JettyServer webserver;

  // Helper
  private HttpURLConnection connect(final URI uri, final String method, final ImmutableMap<String, String> headers, String body) throws Exception
  {
    final HttpURLConnection connection = (HttpURLConnection)uri.toURL().openConnection();
    connection.setRequestMethod(method);
    if (headers != null)
    {
      for (final Map.Entry<String, String> entry : headers.entrySet())
      {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    }
    if (body != null)
    {
      connection.setDoOutput(true);
    }
    connection.setDoInput(true);
    connection.connect();
    if (body != null)
    {
      connection.getOutputStream().write(body.getBytes());
    }
    return connection;
  }

  // Helper
  private String getStringResponse(final HttpURLConnection connection) throws Exception
  {
    InputStream stream = connection.getInputStream();
    InputStreamReader isReader = new InputStreamReader(stream);
    BufferedReader br = new BufferedReader(isReader);
    StringBuffer sb = new StringBuffer();
    String s;
    while ((s = br.readLine()) != null)
    {
      sb.append(s);
    }
    return sb.toString();
  }

  @BeforeClass
  public void setUp() throws Exception
  {
    this.validuri1 = new URI("http://localhost:8080/helloworld");

    // Create an injector with our basic configuration
    final Injector injector = Guice.createInjector(new JerseyServletModule("test.com.wealdtech.jersey.resources"));
    this.webserver = injector.getInstance(JettyServer.class);
    this.webserver.start();
  }

  @AfterClass
  public void tearDown() throws Exception
  {
    this.webserver.stop();
  }

  @Test
  public void testHelloWorld() throws Exception
  {
    // Test valid authentication with a GET request
    final HttpURLConnection connection = connect(this.validuri1, "GET", null, null);
    assertEquals(connection.getResponseCode(), 200);
    final String response = getStringResponse(connection);
    assertEquals(response, "Hello world");
  }

  @Test
  public void testGeoLocation() throws Exception
  {
    final ImmutableMap<String, String> headers = ImmutableMap.of("Geo-Position", "12.123456;6.32524;55 hdg=45");
    final HttpURLConnection connection = connect(this.validuri1, "GET", headers, null);
    assertEquals(connection.getResponseCode(), 200);
  }

  @Test
  public void testHelloWorldDate() throws Exception
  {
    // Test using a DateTime query parameter
    final URI dateTimeUri = new URI("http://localhost:8080/helloworld/date?date=2015-05-14T09:00:00%2B0200+Europe%2FParis");
    final HttpURLConnection connection = connect(dateTimeUri, "GET", null, null);
    assertEquals(connection.getResponseCode(), 200);
    final String response = getStringResponse(connection);
    assertEquals(response, "Hello world, 2015-05-14T09:00:00+02:00");
  }
}

