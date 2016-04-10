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
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.contacts.ContactsClient;
import com.wealdtech.contacts.ContactsClientGoogleContactsImpl;
import com.wealdtech.contacts.config.ContactsConfiguration;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import com.wealdtech.services.google.GoogleAccountsClient;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 *
 */
public class ContactsSyncServiceSynchronousImplTest
{
  private static final String REFRESH_TOKEN = System.getenv("contacts_test_user_refresh_token");

  private ContactsSyncService<OAuth2Credentials> service;
  private GoogleAccountsClient accountsClient;

  @BeforeClass
  public void setUp() throws GeneralSecurityException, IOException
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "contact-test", "contact", "contact", null, null,
                                                             null));

    final ContactService contactService = new ContactServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                                                  .copy()
                                                                                                  .enable(
                                                                                                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));


    final ContactsConfiguration configuration = ContactsConfiguration.fromEnv("contacts_test");

    accountsClient = new GoogleAccountsClient(configuration.getOauth2Configuration());
    final ContactsClient<OAuth2Credentials> contactClient = new ContactsClientGoogleContactsImpl(configuration);

    service = new ContactsSyncServiceSynchronousImpl<OAuth2Credentials>(contactService, contactClient);
  }

  @Test
  public void testImport()
  {
    final OAuth2Credentials credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                                                 .name("Google contacts")
                                                                                 .accessToken("irrelevant")
                                                                                 .expires(DateTime.now().minusDays(1))
                                                                                 .refreshToken(REFRESH_TOKEN)
                                                                                 .build());

    service.importContacts(credentials, false);
  }
}
