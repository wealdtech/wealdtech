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
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 *
 */
public class RelationshipServicePostgreSqlImplTest
{
  private RelationshipService service;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "contact-test", "contact", "contact", null, null, null));

    service = new RelationshipServicePostgreSqlImpl(repository, WealdMapper.getServerMapper()
                                                                           .copy()
                                                                           .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
  }

  @Test
  public void testCRUD()
  {
    WID<Contact> aliceId = WID.generate();
    WID<Contact> bobId = WID.generate();

    Relationship aliceToBob = null;
    try
    {
      aliceToBob = Relationship.builder()
                               .id(WID.<Relationship>generate())
                               .from(aliceId)
                               .to(bobId)
                               .contexts(ImmutableSet.of(Context.builder()
                                                                .situation(Context.Situation.PROFESSIONAL)
                                                                .knownAs(ImmutableSet.of("Mr. Jones", "Bob Jones"))
                                                                .familiarity(30)
                                                                .formality(50)
                                                                .build(),
                                                         Context.builder()
                                                                .situation(Context.Situation.FAMILIAL)
                                                                .knownAs(ImmutableSet.of("Dad"))
                                                                .familiarity(100)
                                                                .formality(0)
                                                                .build()))
                               .build();
      service.create(aliceToBob);

      final Relationship dbAliceToBob = service.obtain(aliceToBob.getId());
      assertEquals(dbAliceToBob, aliceToBob);

      final Relationship aliceToBob2 = Relationship.builder(aliceToBob)
                                                   .contexts(ImmutableSet.of(Context.builder()
                                                                                    .situation(Context.Situation.PROFESSIONAL)
                                                                                    .knownAs(ImmutableSet.of("Mr. Jones", "Bob Jones"))
                                                                                    .familiarity(40)
                                                                                    .formality(50)
                                                                                    .build(),
                                                                             Context.builder()
                                                                                    .situation(Context.Situation.FAMILIAL)
                                                                                    .knownAs(ImmutableSet.of("Dad"))
                                                                                    .familiarity(100)
                                                                                    .formality(0)
                                                                                    .build()))
          .build();

      service.update(aliceToBob2);
      final Relationship dbAliceToBob2 = service.obtain(aliceToBob2.getId());
      assertEquals(dbAliceToBob2, aliceToBob2);
      assertNotEquals(dbAliceToBob2, aliceToBob);
    }
    finally
    {
      if (aliceToBob != null)
      {
        service.remove(aliceToBob);
      }
    }

  }
}
