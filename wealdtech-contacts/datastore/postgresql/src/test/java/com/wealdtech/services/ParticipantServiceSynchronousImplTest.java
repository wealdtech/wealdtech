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
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Participant;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.ParticipantService;
import com.wealdtech.contacts.services.ParticipantServiceSynchronousImpl;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.contacts.uses.EmailUse;
import com.wealdtech.contacts.uses.NameUse;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.repositories.PostgreSqlRepository;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class ParticipantServiceSynchronousImplTest
{
  private RelationshipService relationshipService;
  private ParticipantService service;

  @BeforeClass
  public void setUp()
  {
    final PostgreSqlRepository repository =
        new PostgreSqlRepository(new PostgreSqlConfiguration("localhost", 5432, "test", "test", "test", null, null, null));

    final ObjectMapper mapper = WealdMapper.getServerMapper().copy().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    final ContactService contactService = new ContactServicePostgreSqlImpl(repository, mapper);
    contactService.createDatastore();
    relationshipService = new RelationshipServicePostgreSqlImpl(repository, contactService, mapper);
    service = new ParticipantServiceSynchronousImpl(relationshipService);
  }

  @Test
  public void testRefineAmbiguous()
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
                                                                 .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@work.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.PROFESSIONAL)
                                                                  .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.SOCIAL)
                                                                  .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.FAMILIAL)
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
                                                                 .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Smith")
                                                                  .address("Chris.Smith@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.SOCIAL)
                                                                  .build()))
                                    .build();

    try
    {
      relationshipService.create(aliceToChrisJones);
      relationshipService.create(aliceToChrisSmith);

      // We talk about Chris in a social context; confirm that this is currently ambiguous
      final ImmutableSet<Participant> socialChrises = service.obtain(aliceId, Context.SOCIAL, null, "Chris", null);
      assertEquals(socialChrises.size(), 2);

      // We resolve the ambiguity by saying that we're talking about Chris Smith
      final ImmutableSet<Participant> socialChris = service.obtain(aliceId, Context.SOCIAL, chrisSmithId, "Chris", null);
      assertEquals(socialChris.size(), 1);

      // We ensure that future references are not ambiguous
      final ImmutableSet<Participant> socialChris2 = service.obtain(aliceId, Context.SOCIAL, null, "Chris", null);
      assertEquals(socialChris2.size(), 1);
    }
    finally
    {
      if (aliceToChrisSmith != null)
      {
        relationshipService.remove(aliceToChrisSmith);
      }
      if (aliceToChrisJones != null)
      {
        relationshipService.remove(aliceToChrisJones);
      }
    }
  }

  @Test
  public void testCreateNewName()
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
                                                                 .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@work.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.PROFESSIONAL)
                                                                  .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.SOCIAL)
                                                                  .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Jones")
                                                                  .address("Chris.Jones@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.FAMILIAL)
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
                                                                 .build(),
                                                          EmailUse.builder()
                                                                  .displayName("Chris Smith")
                                                                  .address("Chris.Smith@home.com")
                                                                  .formality(50)
                                                                  .familiarity(1)
                                                                  .context(Context.SOCIAL)
                                                                  .build()))
                                    .build();

    try
    {
      relationshipService.create(aliceToChrisJones);
      relationshipService.create(aliceToChrisSmith);

      // We fetch Chris Jones with a known email address but new name
      final ImmutableSet<Participant> socialChrises = service.obtain(aliceId, Context.SOCIAL, null, "Chrissy", "Chris.Jones@home.com");
      assertEquals(socialChrises.size(), 1);
      assertEquals(socialChrises.iterator().next().getRelationshipId(), aliceToChrisJones.getId());

      // We can now fetch Chris Jones with the new name
      final ImmutableSet<Participant> socialChris = service.obtain(aliceId, Context.SOCIAL, null, "Chrissy", null);
      assertEquals(socialChris.size(), 1);
      assertEquals(socialChris.iterator().next().getRelationshipId(), aliceToChrisJones.getId());
    }
    finally
    {
      if (aliceToChrisSmith != null)
      {
        relationshipService.remove(aliceToChrisSmith);
      }
      if (aliceToChrisJones != null)
      {
        relationshipService.remove(aliceToChrisJones);
      }
    }
  }
}
