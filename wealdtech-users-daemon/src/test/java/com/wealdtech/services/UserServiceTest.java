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

import com.google.common.collect.ImmutableSet;
import com.wealdtech.*;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.datastore.repositories.ApplicationRepositoryPostgreSqlImpl;
import com.wealdtech.repositories.UserRepositoryPostgreSqlImpl;
import com.wealdtech.utils.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class UserServiceTest
{
  private ApplicationService applicationService;
  private UserService userService;

  private String salt;
  private Application application = null;

  @BeforeClass
  public void setUp()
  {
    salt = StringUtils.generateRandomString(6);
    applicationService =
        new ApplicationServicePostgreSqlImpl(new ApplicationRepositoryPostgreSqlImpl(new PostgreSqlConfiguration("localhost", 5432,
                                                                                                                 "user",
                                                                                                                 "user",
                                                                                                                 "user",
                                                                                                                 null, 1, 1L)),
                                             WObject.getObjectMapper());

    application = Application.builder().id(WID.<Application>generate()).name("User Service Test").ownerId("1").build();
    applicationService.create(application);
    userService =
        new UserServicePostgreSqlImpl(new UserRepositoryPostgreSqlImpl(new PostgreSqlConfiguration("localhost", 5432, "user",
                                                                                                   "user", "user",
                                                                                                   null, 1, 1L)),
                                      WObject.getObjectMapper());
  }

  @AfterClass
  public void tearDown()
  {
    if (application != null)
    {
      applicationService.remove(application);
    }
  }

  @Test()
  public void testCreate()
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName() + "_" + salt;
    User user = null;
    try
    {
      user = User.builder()
                 .id(WID.<User>generate())
                 .name(testName)
                 .emails(ImmutableSet.of(Email.builder().address("test@test.wealdtech.com").primary(true).verified(true).build()))
                 .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                    .scope(AuthorisationScope.FULL)
                                                                                    .password("test")
                                                                                    .build()))
                 .build();

      userService.create(user);

      final User dbUser = userService.obtain(user.getId());

      assertEquals(User.serialize(dbUser), User.serialize(user));
    }
    finally
    {
      if (user != null)
      {
        userService.remove(user);
      }
    }
  }

}
