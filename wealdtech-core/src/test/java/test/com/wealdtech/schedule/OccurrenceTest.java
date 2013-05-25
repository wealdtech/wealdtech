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

package test.com.wealdtech.schedule;

import static org.testng.Assert.*;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.wealdtech.schedule.Occurrence;

public class OccurrenceTest
{
  @Test
  public void testModel() throws Exception
  {
    final Occurrence oc1 = new Occurrence(new DateTime(2012, 1, 5, 8, 0), new DateTime(2012, 1, 5, 9, 0));
    assertNotNull(oc1);
    oc1.getEnd();
    oc1.toString();
    oc1.hashCode();
    assertNotEquals(null, oc1);
    assertEquals(oc1, oc1);

    final Occurrence oc2 = new Occurrence(new DateTime(2012, 6, 26, 8, 0), new DateTime(2012, 6, 26, 9, 0));
    assertNotNull(oc2);
    oc2.getEnd();
    oc2.toString();
    oc2.hashCode();
    assertNotEquals(null, oc2);
    assertEquals(oc2, oc2);
    assertNotEquals(oc1, oc2);
  }
}
