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
import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.handles.NameHandle;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.joda.time.LocalDateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 *
 */
public class ContactServicePostgreSqlImplTest
{
  private ContactService service;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "contact-test", "contact", "contact", null, null, null));

    service = new ContactServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                      .copy()
                                                                      .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
  }

  @Test
  public void testCRUD()
  {
    Contact alice = null;
    Contact bob = null;
    try
    {
      alice = Contact.builder()
                     .id(WID.<Contact>generate())
                     .handles(
                         ImmutableSet.of(NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Alice Jones").build()))
                     .build();
      service.create(alice);
      bob = Contact.builder()
                   .id(WID.<Contact>generate())
                   .handles(ImmutableSet.of(NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Bob").build()))
                   .build();
      service.create(bob);

      final Contact dbAlice = service.obtain(alice.getId());
      assertEquals(dbAlice, alice);

      final Contact dbBob = service.obtain(bob.getId());
      assertEquals(dbBob, bob);

      final Contact alice2 = Contact.builder(alice)
                                    .handles(ImmutableSet.of(NameHandle.builder()
                                                                       .validFrom(LocalDateTime.parse("1970-01-01"))
                                                                       .validTo(LocalDateTime.parse("1995-01-01"))
                                                                       .name("Alice Jones")
                                                                       .build(), NameHandle.builder()
                                                                                           .validFrom(
                                                                                               LocalDateTime.parse("1995-01-01"))
                                                                                           .name("Alice King")
                                                                                           .build()))
                                    .build();
      service.update(alice2);
      final Contact dbAlice2 = service.obtain(alice2.getId());
      assertEquals(dbAlice2, alice2);
      assertNotEquals(dbAlice2, alice);
    }
    finally
    {
      if (bob != null)
      {
        service.remove(bob);
      }
      if (alice != null)
      {
        service.remove(alice);
      }
    }

  }
}
