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
import com.wealdtech.DataError;
import com.wealdtech.ServerError;

/**
 * Interface to provide a principal from a unique key.
 *
 * @param <T> the type of the principal
 * @param <C> the type of the key passed to locate the principal
 */
public interface PrincipalProvider<T, C>
{
  public Optional<T> get(C key) throws DataError, ServerError;
}