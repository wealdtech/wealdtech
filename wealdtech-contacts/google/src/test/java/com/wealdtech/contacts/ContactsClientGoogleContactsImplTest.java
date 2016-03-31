/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.google.common.collect.ImmutableList;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.contacts.config.ContactsConfiguration;
import com.wealdtech.services.google.GoogleAccountsClient;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 *
 */
public class ContactsClientGoogleContactsImplTest
{
  private static final String REFRESH_TOKEN = System.getenv("contacts_test_user_refresh_token");

  @Test
  public void testObtainContacts() throws IOException, GeneralSecurityException
  {
    final ContactsConfiguration configuration = ContactsConfiguration.fromEnv("contacts_test");

    final GoogleAccountsClient accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                                                 .name("Google contacts")
                                                                                 .accessToken("irrelevant")
                                                                                 .expires(DateTime.now().minusDays(1))
                                                                                 .refreshToken(REFRESH_TOKEN)
                                                                                 .build());
    final ContactsClient client = new ContactsClientGoogleContactsImpl(configuration);
    final ImmutableList<Contact> contacts = client.obtainContacts(credentials);

    for (final Contact contact : contacts)
    {
      System.err.println(contact);
    }
  }
}
