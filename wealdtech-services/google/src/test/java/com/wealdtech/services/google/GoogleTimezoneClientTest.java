/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class GoogleTimezoneClientTest
{
  @Test
  public void testSimple()
  {
    final GoogleTimezoneClient client = GoogleTimezoneClient.getInstance();

    final DateTimeZone timezone = client.timezone(System.getenv("wealdtech_config_google_api"), 28.431157, -81.308083);
    assertEquals(timezone, DateTimeZone.forID("America/New_York"));
  }

}
