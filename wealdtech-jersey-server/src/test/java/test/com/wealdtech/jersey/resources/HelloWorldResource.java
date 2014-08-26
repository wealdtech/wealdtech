/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */
package test.com.wealdtech.jersey.resources;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * (Very) simple resource for testing
 */
@Path("helloworld")
public class HelloWorldResource
{
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

  @GET
  @Produces("text/plain")
  public String getHelloWorld()
  {
    return "Hello world";
  }

  @GET
  @Path("date")
  @Produces("text/plain")
  public String getHelloWorldDate(@QueryParam("date") final DateTime date)
  {
    return "Hello world, " + formatter.print(date);
  }
}
