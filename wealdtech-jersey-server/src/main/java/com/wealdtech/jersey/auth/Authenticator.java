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

package com.wealdtech.jersey.auth;

import com.google.common.base.Optional;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Generic interface for authentication of a principal for a given request.
 */
public interface Authenticator<T>
{
  /**
   * State if this authenticator can authenticate the given request
   * @param request the request which is to be authenticated
   * @return <code>true</code> if the authenticator can be used for this request, <code>false</code> otherwise
   */
  boolean canAuthenticate(final ContainerRequest request);

  /**
   * Authenticate a principal given a set of credentials.
   * @param request the request which is to be authenticated
   * @return The authenticated principal, or Optional.absent() if not authenticated
   */
  Optional<T> authenticate(final ContainerRequest request);
}
