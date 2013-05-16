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

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.wealdtech.schedule.Occurrence;
import com.wealdtech.schedule.Schedule;
import com.wealdtech.utils.Accessor;

public class ScheduleAccessorTest
{
  private Schedule rs1;

  @BeforeClass
  public void setUp()
  {
    this.rs1 = new Schedule.Builder()
                           .start(new DateTime(2012, 1, 5, 1, 0))
                           .duration(new Period(Hours.ONE))
                           .weeksOfYear(Schedule.ALL)
                           .daysOfWeek(ImmutableList.of(1, 2, 3, 4, 5))
                           .build();
  }

  @Test
  public void testModel() throws Exception
  {
    final Accessor<Occurrence, DateTime> accessor = this.rs1.accessor();
    for (int i = 0; i < 15; i++)
    {
      System.out.println(accessor.next());
    }
  }
}
