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
package test.com.wealdtech.jersey;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;


/**
 * A simple HTTP server to test Hawk authentication
 */
public class GrizzlyServer
{
  protected static HttpServer startServer() throws IllegalArgumentException, NullPointerException, IOException, URISyntaxException
  {
    final URI baseuri = new URI("http://localhost:18233/");
    final ResourceConfig rc = new PackagesResourceConfig("com.wealdtech.jersey.providers", "test.com.wealdtech.jersey.resources");
    return GrizzlyServerFactory.createHttpServer(baseuri, rc);
  }

  public static void main(String[] args) throws InterruptedException, IOException, IllegalArgumentException, NullPointerException, URISyntaxException
  {
    final HttpServer httpServer = startServer();
    System.in.read();
    httpServer.stop();
  }
}
