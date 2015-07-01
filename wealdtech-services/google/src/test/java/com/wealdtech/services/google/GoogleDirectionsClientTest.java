/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services.google;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class GoogleDirectionsClientTest
{
  @Test
  public void testSimple()
  {
    final GoogleDirectionsClient client = new GoogleDirectionsClient(GoogleServicesConfiguration.fromEnv("wealdtech_config_google"));

    final Integer duration =
        client.getDurationOfJourney(50837551L, 471511L, 50861544L, -83385L, "driving",
                                    new DateTime().plusYears(1).getMillis() / 1000);
    assertEquals(duration, (Integer)2631, "Incorrect duration");
  }

}
