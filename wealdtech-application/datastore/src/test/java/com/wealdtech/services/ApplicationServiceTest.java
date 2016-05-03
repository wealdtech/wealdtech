/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.wealdtech.Application;
import com.wealdtech.WID;
import com.wealdtech.WObject;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repositories.ApplicationRepositoryPostgreSqlImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class ApplicationServiceTest
{
  private ApplicationService applicationService;

  @BeforeClass
  public void setUp()
  {
    applicationService =
        new ApplicationServicePostgreSqlImpl(new ApplicationRepositoryPostgreSqlImpl(new PostgreSqlConfiguration("localhost", 5432,
                                                                                                                 "test",
                                                                                                                 "test",
                                                                                                                 "test",
                                                                                                                 null, 1, 1L)),
                                             WObject.getObjectMapper());
  }

  @Test()
  public void testCreate()
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName();
    Application application = null;
    try
    {
      application = Application.builder().id(WID.<Application>generate()).name(testName).ownerId(WID.generate().toString()).build();

      applicationService.create(application);

      final Application dbApplication = applicationService.obtain(application.getId());

      assertEquals(Application.serialize(dbApplication), Application.serialize(application));
    }
    finally
    {
      if (application != null)
      {
        applicationService.remove(application);
      }
    }
  }

}
