/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.DataError;
import com.wealdtech.Email;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.AuthenticationMethod;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.UserRepositoryPostgreSqlImpl;
import com.wealdtech.utils.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UserServicePostgreSqlImplTest
{
  private String salt;
  private UserService<?> service;

  @BeforeClass
  public void setUp()
  {
    salt = "_" + StringUtils.generateRandomString(6);

    final PostgreSqlConfiguration postgreSqlConfiguration =
        new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null);

    service = new UserServicePostgreSqlImpl(new UserRepositoryPostgreSqlImpl(postgreSqlConfiguration), WealdMapper.getServerMapper()
                                                                                                                  .copy()
                                                                                                                  .enable(
                                                                                                                      SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
  }

  @Test
  public void testSimpleUpdate()
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName() + salt;
    final ImmutableSet<AuthenticationMethod> authenticationMethods = ImmutableSet.<AuthenticationMethod>of(PasswordAuthenticationMethod.builder().password("foo").scope(
        AuthorisationScope.FULL).build());
    final ImmutableSet<Email> emails = ImmutableSet.of(Email.builder().address(testName+salt+"@test.com").primary(true).verified(true).build());
    User user = User.builder().id(WID.<User>generate()).name("test").emails(emails).authenticationMethods(authenticationMethods).build();
    service.create(user);
    User dbUser = service.obtain(user.getId());
    assertEquals(dbUser, user);

    for (int i = 2; i < 1000; i++)
    {
      user = User.builder(user).name("Update " + i).build();
      service.update(user);
      assertEquals((int)service.obtain(user.getId()).getVersion(), i);
    }
  }

  @Test(expectedExceptions = {DataError.Bad.class})
  public void testBadUpdate()
  {
    final String testName = new Object() {}.getClass().getEnclosingMethod().getName() + "_" + salt;
    final ImmutableSet<AuthenticationMethod> authenticationMethods = ImmutableSet.<AuthenticationMethod>of(PasswordAuthenticationMethod.builder().password("foo").scope(
        AuthorisationScope.FULL).build());
    final ImmutableSet<Email> emails = ImmutableSet.of(Email.builder().address(testName+salt+"@test.com").primary(true).verified(true).build());
    User user = User.builder().data("_version", 10).id(WID.<User>generate()).name("test").emails(emails).authenticationMethods(authenticationMethods).build();
    service.create(user);

    User badUpdatedUser = User.builder(service.obtain(user.getId())).data("_version", 1).build();

    service.update(badUpdatedUser);
  }

}
