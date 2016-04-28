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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.contacts.uses.NameUse;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repository.ContactRepositoryPostgreSqlImpl;
import com.wealdtech.repository.RelationshipRepositoryPostgreSqlImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 *
 */
public class RelationshipServicePostgreSqlImplTest
{
  private RelationshipService<?> service;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlConfiguration postgreSqlConfiguration =
        new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null);

    final ObjectMapper mapper = WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    final ContactService contactService =
        new ContactServicePostgreSqlImpl(new ContactRepositoryPostgreSqlImpl(postgreSqlConfiguration), mapper);
    contactService.createDatastore();
    service =
        new RelationshipServicePostgreSqlImpl(new RelationshipRepositoryPostgreSqlImpl(postgreSqlConfiguration), contactService,
                                              mapper);
    service.createDatastore();
  }

  @Test
  public void testCRUD()
  {
    WID<User> aliceId = WID.generate();

    Relationship aliceToBob = null;
    try
    {
      aliceToBob = Relationship.builder()
                               .id(WID.<Relationship>generate())
                               .ownerId(aliceId)
                               .to(WID.<Contact>generate())
                               .uses(ImmutableSet.of(NameUse.builder()
                                                            .name("Mr. Jones")
                                                            .familiarity(50)
                                                            .formality(50)
                                                            .context(Context.PROFESSIONAL)
                                                            .build(), NameUse.builder()
                                                                             .name("Bob Jones")
                                                                             .familiarity(20)
                                                                             .formality(50)
                                                                             .context(Context.PROFESSIONAL)
                                                                             .build(), NameUse.builder()
                                                                                              .name("Dad")
                                                                                              .familiarity(100)
                                                                                              .formality(0)
                                                                                              .context(Context.FAMILIAL)
                                                                                              .build()))
                               .build();
      service.create(aliceToBob);

      final Relationship dbAliceToBob = service.obtain(aliceId, aliceToBob.getId());
      assertEquals(dbAliceToBob, aliceToBob);

      final Relationship aliceToBob2;
      aliceToBob2 = Relationship.builder(aliceToBob)
                                .uses(ImmutableSet.of(NameUse.builder()
                                                             .name("Mr. Jones")
                                                             .familiarity(50)
                                                             .formality(50)
                                                             .context(Context.PROFESSIONAL)
                                                             .build(), NameUse.builder()
                                                                              .name("Bob Jones")
                                                                              .familiarity(30)
                                                                              .formality(50)
                                                                              .context(Context.PROFESSIONAL)
                                                                              .build(), NameUse.builder()
                                                                                               .name("Dad")
                                                                                               .familiarity(100)
                                                                                               .formality(0)
                                                                                               .context(Context.FAMILIAL)
                                                                                               .build()))
                                .build();

      service.update(aliceToBob2);
      final Relationship dbAliceToBob2 = service.obtain(aliceId, aliceToBob2.getId());
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

  @Test
  public void testUnambiguousMatch()
  {
    WID<User> aliceId = WID.generate();
    WID<Contact> chrisJonesId = WID.generate();
    WID<Contact> chrisSmithId = WID.generate();
    WID<Contact> chrisThomasId = WID.generate();

    Relationship aliceToChrisJones;
    Relationship aliceToChrisSmith;
    Relationship aliceToChrisThomas;

    aliceToChrisJones = Relationship.builder()
                                    .id(WID.<Relationship>generate())
                                    .ownerId(aliceId)
                                    .to(chrisJonesId)
                                    .uses(ImmutableSet.of(NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(50)
                                                                 .familiarity(50)
                                                                 .context(Context.PROFESSIONAL)
                                                                 .build(),
                                                          NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(10)
                                                                 .familiarity(50)
                                                                 .context(Context.SOCIAL)
                                                                 .build()))
                                                .build();
    aliceToChrisSmith = Relationship.builder()
                                    .id(WID.<Relationship>generate())
                                    .ownerId(aliceId)
                                    .to(chrisSmithId)
                                    .uses(ImmutableSet.of(NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(30)
                                                                 .familiarity(30)
                                                                 .context(Context.SOCIAL)
                                                                 .build()))
                                    .build();
    aliceToChrisThomas = Relationship.builder()
                                     .id(WID.<Relationship>generate())
                                     .ownerId(aliceId)
                                     .to(chrisThomasId)
                                     .uses(ImmutableSet.of(NameUse.builder()
                                                                  .name("Chris")
                                                                  .formality(10)
                                                                  .familiarity(30)
                                                                  .context(Context.FAMILIAL)
                                                                  .build()))
                                     .build();
    try
    {
      service.create(aliceToChrisJones);
      service.create(aliceToChrisSmith);
      service.create(aliceToChrisThomas);

      final ImmutableList<Relationship> socialChrises = service.obtain(aliceId, Context.SOCIAL, null, "Chris", null);
      assertEquals(socialChrises.size(), 1);
      assertEquals(socialChrises.iterator().next().getTo(), chrisJonesId);
      final ImmutableList<Relationship> professionalChrises = service.obtain(aliceId, Context.PROFESSIONAL, null, "Chris", null);
      assertEquals(professionalChrises.size(), 1);
      assertEquals(professionalChrises.iterator().next().getTo(), chrisJonesId);
      final ImmutableList<Relationship> familialChrises = service.obtain(aliceId, Context.FAMILIAL, null, "Chris", null);
      assertEquals(familialChrises.size(), 1);
      assertEquals(familialChrises.iterator().next().getTo(), chrisThomasId);
    }
    finally
    {
      if (aliceToChrisThomas != null)
      {
        service.remove(aliceToChrisThomas);
      }
      if (aliceToChrisSmith != null)
      {
        service.remove(aliceToChrisSmith);
      }
      if (aliceToChrisJones != null)
      {
        service.remove(aliceToChrisJones);
      }
    }
  }

  @Test
  public void testAmbiguousMatch()
  {
    WID<User> aliceId = WID.generate();
    WID<Contact> chrisJonesId = WID.generate();
    WID<Contact> chrisSmithId = WID.generate();

    Relationship aliceToChrisJones;
    Relationship aliceToChrisSmith;

    aliceToChrisJones = Relationship.builder()
                                    .id(WID.<Relationship>generate())
                                    .ownerId(aliceId)
                                    .to(chrisJonesId)
                                    .uses(ImmutableSet.of(NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(50)
                                                                 .familiarity(1)
                                                                 .context(Context.PROFESSIONAL)
                                                                 .build(),
                                                          NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(50)
                                                                 .familiarity(1)
                                                                 .context(Context.SOCIAL)
                                                                 .build()))
                                                .build();
    aliceToChrisSmith = Relationship.builder()
                                    .id(WID.<Relationship>generate())
                                    .ownerId(aliceId)
                                    .to(chrisSmithId)
                                    .uses(ImmutableSet.of(NameUse.builder()
                                                                 .name("Chris")
                                                                 .formality(50)
                                                                 .familiarity(1)
                                                                 .context(Context.SOCIAL)
                                                                 .build()))
                                    .build();

    try
    {
      service.create(aliceToChrisJones);
      service.create(aliceToChrisSmith);

      final ImmutableList<Relationship> socialChrises = service.obtain(aliceId, Context.SOCIAL, null, "Chris", null);
      assertEquals(socialChrises.size(), 2);
      final ImmutableList<Relationship> professionalChrises = service.obtain(aliceId, Context.PROFESSIONAL, null, "Chris", null);
      assertEquals(professionalChrises.size(), 1);
      assertEquals(professionalChrises.iterator().next().getTo(), chrisJonesId);
      final ImmutableList<Relationship> familialChrises = service.obtain(aliceId, Context.FAMILIAL, null, "Chris", null);
      assertEquals(familialChrises.size(), 0);
    }
    finally
    {
      if (aliceToChrisSmith != null)
      {
        service.remove(aliceToChrisSmith);
      }
      if (aliceToChrisJones != null)
      {
        service.remove(aliceToChrisJones);
      }
    }
  }
}
