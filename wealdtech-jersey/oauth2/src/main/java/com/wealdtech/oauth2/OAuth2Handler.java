/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.oauth2;

import java.net.URI;

/**
 *
 */
public interface OAuth2Handler
{
  /**
   * Generate the authorisation URI for this provider
   *
   * @param id an ID that will identify the user when the callback returns
   * @return A URI that will provide the correct request for authorisation against the provider
   */
  URI generateAuthorisationUri(String id);

  /**
   * Authorise a request
   * @param uri the URI that triggered the authorisation callback
   */
  void handleAuthorisation(URI uri);
}
