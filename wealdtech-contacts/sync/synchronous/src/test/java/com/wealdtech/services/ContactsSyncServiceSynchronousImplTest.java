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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.contacts.ContactsClient;
import com.wealdtech.contacts.ContactsClientGoogleContactsImpl;
import com.wealdtech.contacts.config.ContactsConfiguration;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repository.ContactRepositoryPostgreSqlImpl;
import com.wealdtech.repository.RelationshipRepositoryPostgreSqlImpl;
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
    final PostgreSqlConfiguration postgreSqlConfiguration =
        new PostgreSqlConfiguration("localhost", 5432, "ellie", "ellie", "ellie", null, null, null);

    final ObjectMapper mapper = WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    final ContactService contactService =
        new ContactServicePostgreSqlImpl(new ContactRepositoryPostgreSqlImpl(postgreSqlConfiguration), mapper);
    final RelationshipService relationshipService =
        new RelationshipServicePostgreSqlImpl(new RelationshipRepositoryPostgreSqlImpl(postgreSqlConfiguration), contactService,
                                              mapper);

    final ContactsConfiguration configuration = ContactsConfiguration.fromEnv("contacts_test");

    accountsClient = new GoogleAccountsClient(configuration.getOAuth2Configuration());
    final ContactsClient<OAuth2Credentials> contactClient = new ContactsClientGoogleContactsImpl(configuration);

    service = new ContactsSyncServiceSynchronousImpl<>(contactService, relationshipService, contactClient);
  }

  @Test
  public void testImport()
  {
    final WID<User> userId = WID.fromString("7edadd9464485b9");
    final OAuth2Credentials credentials;
    credentials = accountsClient.reauth(OAuth2Credentials.builder()
                                                         .name("Google contacts")
                                                         .accessToken("irrelevant")
                                                         .expires(DateTime.now().minusDays(1))
                                                         .scopes(ImmutableSet.of("https://www.googleapis.com/auth/calendar",
                                                                                 "https://www.googleapis.com/auth/contacts",
                                                                                 "profile", "email"))
                                                         .refreshToken(REFRESH_TOKEN)
                                                         .build());

    service.importContacts(userId, credentials, false);
  }
}
