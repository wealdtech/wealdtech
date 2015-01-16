/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
import com.wealdtech.services.WObjectService;
import com.wealdtech.services.WObjectServiceCallbackPostgreSqlImpl;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 */
public class WObjectPostgreSqlTest
{
  WObjectService<TestWObject, PreparedStatement> service;

  public static class TestWObject extends WObject<TestWObject>
  {
    @JsonCreator
    public TestWObject(final ImmutableMap<String, Object> data)
    {
      super(data);
    }

    public static class Builder<P extends Builder<P>> extends WObject.Builder<TestWObject, P>
    {
      public TestWObject build()
      {
        return new TestWObject(ImmutableMap.copyOf(data));
      }
    }
    public static Builder<?> builder() { return new Builder(); }
  };

  public class TestObjectServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<TestWObject>
  {
    @Inject
    public TestObjectServicePostgreSqlImpl(final PostgreSqlRepository repository)
    {
      super(repository, "test");
    }
  }

  @BeforeClass
  public void setUp()
  {
    PostgreSqlRepository repository = new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null));
    service = new TestObjectServicePostgreSqlImpl(repository);
    service.createDatastore();
  }

  @AfterClass
  public void tearDown()
  {
    if (service != null)
    {
      service.destroyDatastore();
    }
  }

  @Test
  public void testAdd()
  {
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();

    final TestWObject testObj = TestWObject.builder()
                              .data("test string", "test value")
                                    .data("test date", new DateTime(1234567890000L))
                              .build();
    service.add(testObj);

    final ImmutableList<TestWObject> testObjs = service.obtain(new TypeReference<TestWObject>(){}, null);
    assertNotNull(testObjs);
    assertEquals(testObjs.size(), 1);
  }

  @Test
  public void testConditionalObtain()
  {
    final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
    final TestWObject testObj1 = TestWObject.builder()
                                            .data("val", "foo")
                                            .build();
    service.add(testObj1);
    final TestWObject testObj2 = TestWObject.builder()
                                            .data("val", "bar")
                                            .build();
    service.add(testObj2);
    final TestWObject testObj3 = TestWObject.builder()
                                            .data("val", "foo")
                                            .build();
    service.add(testObj3);

    final ImmutableList<TestWObject> testObjs = service.obtain(new TypeReference<TestWObject>(){}, new WObjectServiceCallbackPostgreSqlImpl(){
      @Override
      public String getConditions()
      {
        return "f_data->>'val'=?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setString(stmt, index++, "foo");
      }
    });

    assertNotNull(testObjs);
    assertEquals(testObjs.size(), 2);
  }
}
