/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.Application;
import com.wealdtech.Email;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.AuthorisationScope;
import com.wealdtech.authentication.PasswordAuthenticationMethod;
import com.wealdtech.chat.config.ApplicationModule;
import com.wealdtech.guice.EventBusAsynchronousModule;
import com.wealdtech.services.ApplicationService;
import com.wealdtech.services.UserService;
import com.wealdtech.utils.StringUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Setup and teardown methods for the test suite
 */
public class ChatDTest
{
  private String salt;

  private ApplicationService applicationService;
  private UserService userService;

  public static Application application;
  public static User user1, user2, user3;

  @BeforeSuite
  public void setUp()
  {
    // Create an application and users for testing
    ChatD.main(null);

    final Injector injector = Guice.createInjector(new ApplicationModule("chatd-config.json"), new EventBusAsynchronousModule());

    salt = "_" + StringUtils.generateRandomString(6);

    applicationService = injector.getInstance(ApplicationService.class);
    userService = injector.getInstance(UserService.class);

    user1 = User.builder()
                .id(WID.<User>generate())
                .name("User 1")
                .emails(ImmutableSet.of(Email.builder()
                                             .address("user1" + salt + "@test.wealdtech.com")
                                             .primary(true)
                                             .verified(true)
                                             .build()))
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .password("test")
                                                                                   .build()))
                .build();
    userService.create(user1);

    user2 = User.builder()
                .id(WID.<User>generate())
                .name("User 2")
                .emails(ImmutableSet.of(Email.builder()
                                             .address("user2" + salt + "@test.wealdtech.com")
                                             .primary(true)
                                             .verified(true)
                                             .build()))
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .password("test")
                                                                                   .build()))
                .build();
    userService.create(user2);

    user3 = User.builder()
                .id(WID.<User>generate())
                .name("User 3")
                .emails(ImmutableSet.of(Email.builder()
                                             .address("user3" + salt + "@test.wealdtech.com")
                                             .primary(true)
                                             .verified(true)
                                             .build()))
                .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                   .scope(AuthorisationScope.FULL)
                                                                                   .password("test")
                                                                                   .build()))
                .build();
    userService.create(user3);

    application = Application.builder().id(WID.<Application>generate()).name("ChatD tests" + salt).ownerId(user1.getId().toString()).build();
    applicationService.create(application);
  }

  @AfterSuite
  public void tearDown()
  {
    if (user3 != null) { userService.remove(user3); }
    if (user2 != null) { userService.remove(user2); }
    if (user1 != null) { userService.remove(user1); }
    if (application != null) { applicationService.remove(application); }
  }
}
