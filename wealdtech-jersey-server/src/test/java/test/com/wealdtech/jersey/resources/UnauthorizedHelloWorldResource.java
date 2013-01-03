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
package test.com.wealdtech.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wealdtech.jersey.exceptions.UnauthorizedException;

/**
 * (Very) simple resource for testing unauthorized exceptions
 */
@Path("unauthorizedhelloworld")
public class UnauthorizedHelloWorldResource
{
  @GET
  @Produces("text/plain")
  public String getHelloWorld()
  {
    throw new UnauthorizedException("Not allowed to see this");
  }
}
