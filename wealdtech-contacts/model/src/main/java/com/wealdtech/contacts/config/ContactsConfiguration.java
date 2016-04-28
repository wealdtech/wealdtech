/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.configuration.Configuration;
import com.wealdtech.configuration.OAuth2Configuration;

/**
 *
 */
public class ContactsConfiguration implements Configuration
{
  private final String productId;
  private final OAuth2Configuration oauth2;

  @JsonCreator
  public ContactsConfiguration(@JsonProperty("productid") final String productId,
                               @JsonProperty("oauth2") final OAuth2Configuration oauth2)
  {
    this.productId = productId;
    this.oauth2 = oauth2;
  }

  /**
   * Obtain a configuration from the environment
   *
   * @param base the base string to use as the prefix for obtaining environmental variables
   */
  public static ContactsConfiguration fromEnv(final String base)
  {
    return new ContactsConfiguration(System.getenv(base + "_productid"), OAuth2Configuration.fromEnv(base + "_oauth2"));
  }

  public String getProductId() { return productId; }

  public OAuth2Configuration getOAuth2Configuration() { return oauth2; }
}
