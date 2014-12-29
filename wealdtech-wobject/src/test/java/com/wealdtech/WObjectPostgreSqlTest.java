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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repository.PostgreSqlRepository;
import com.wealdtech.services.WObjectService;
import com.wealdtech.services.WObjectServicePostgreSqlImpl;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 */
public class WObjectPostgreSqlTest
{
  WObjectService<TestWObject> service;

  public static class TestWObject extends WObject<TestWObject>
  {
    @JsonCreator
    public TestWObject(@JsonProperty("data") final Map<String, Object> data)
    {
      super(data);
    }

    public static class Builder extends WObject.Builder<TestWObject.Builder>
    {
      public TestWObject build()
      {
        return new TestWObject(data);
      }
    }
    public static Builder builder() { return new Builder(); }
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
}
