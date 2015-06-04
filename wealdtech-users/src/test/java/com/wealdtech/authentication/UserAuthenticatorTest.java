/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.authentication;

import com.google.common.collect.ImmutableSet;
import com.wealdtech.Email;
import com.wealdtech.User;
import com.wealdtech.WID;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
public class UserAuthenticatorTest
{
  private User testUser1;

  @BeforeClass
  public void setUp()
  {
    testUser1 = User.builder()
                    .id(WID.<User>generate())
                    .name("test user 1")
                    .emails(ImmutableSet.of(Email.builder().address("test@test.com").primary(true).verified(true).build()))
                    .authenticationMethods(ImmutableSet.of(PasswordAuthenticationMethod.builder()
                                                                                       .password("testpassword")
                                                                                       .scope(AuthorisationScope.FULL)
                                                                                       .build(), TokenAuthenticationMethod.builder()
                                                                                                                          .token("token")
                                                                                                                          .scope(AuthorisationScope.FULL)
                                                                                                                          .build()))
                    .build();
  }

  @Test
  public void testMatchingPassword()
  {
    Credentials testCredentials = PasswordCredentials.builder().name("testuser").password("testpassword").build();
    final AuthorisationScope scope = new UserAuthenticator(testUser1).authenticate(testCredentials);
    assertEquals(scope, AuthorisationScope.FULL);
  }

  @Test
  public void testNonMatchingPassword()
  {
    Credentials testCredentials = PasswordCredentials.builder().name("testuser").password("badpassword").build();
    final AuthorisationScope scope = new UserAuthenticator(testUser1).authenticate(testCredentials);
    assertEquals(scope, AuthorisationScope.NONE);
  }
}
