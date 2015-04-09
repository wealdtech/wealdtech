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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class TokenAuthenticator implements Authenticator
{
  private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticator.class);

  @Override
  public AuthorisationScope authenticate(final Credentials credentials, final ImmutableCollection<AuthenticationMethod> methods)
  {
    AuthorisationScope scope = AuthorisationScope.NONE;

    final String credentialsType = credentials.getType();
    // Ensure that we are dealing with token credentials
    if (Objects.equal(credentialsType, TokenCredentials.TOKEN_CREDENTIALS))
    {
      final TokenCredentials tokenCredentials = (TokenCredentials)credentials;
      for (final AuthenticationMethod method : methods)
      {
        if (Objects.equal(method.getType(), TokenAuthenticationMethod.TOKEN_AUTHENTICATION))
        {
          final TokenAuthenticationMethod tokenMethod = (TokenAuthenticationMethod)method;
          if (Objects.equal(tokenMethod.getToken(), tokenCredentials.getToken()))
          {
            scope = tokenMethod.getScope();
            break;
          }
        }
      }
    }

    return scope;
  }
}
