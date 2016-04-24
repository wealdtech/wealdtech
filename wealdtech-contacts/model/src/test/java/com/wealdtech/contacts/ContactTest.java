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

import com.google.common.collect.ImmutableSet;
import com.wealdtech.WID;
import com.wealdtech.contacts.handles.NameHandle;
import com.wealdtech.contacts.uses.NameUse;
import org.joda.time.LocalDateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test of the contact model
 */
public class ContactTest
{
  ContactsClient client = null;

  @BeforeClass
  public void setUp()
  {

  }

  @Test
  public void testRelationship()
  {
    final Contact alice = Contact.builder()
                                 .id(WID.<Contact>generate())
                                 .handles(ImmutableSet.of(
                                     NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Alice").build()))
                                 .build();
    final Contact bob = Contact.builder()
                               .id(WID.<Contact>generate())
                               .handles(ImmutableSet.of(
                                   NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Bob").build()))
                               .build();

    final Relationship aliceToBob = Relationship.builder()
                                                .from(alice.getId())
                                                .to(bob.getId())
                                                .uses(ImmutableSet.of(NameUse.builder()
                                                                             .name("Mr. Jones")
                                                                             .familiarity(50)
                                                                             .formality(50)
                                                                             .context(Context.PROFESSIONAL)
                                                                             .build(),
                                                                      NameUse.builder()
                                                                             .name("Bob Jones")
                                                                             .familiarity(20)
                                                                             .formality(50)
                                                                             .context(Context.PROFESSIONAL)
                                                                             .build(),
                                                                      NameUse.builder()
                                                                             .name("Dad")
                                                                             .familiarity(100)
                                                                             .formality(0)
                                                                             .context(Context.FAMILIAL)
                                                                             .build()))
                                                .build();

    assertEquals(aliceToBob.getFrom(), alice.getId());
    assertEquals(aliceToBob.getTo(), bob.getId());
  }

  @Test
  public void testFindByHandle()
  {
    final Contact alice = Contact.builder()
                                 .id(WID.<Contact>generate())
                                 .handles(ImmutableSet.of(
                                     NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Alice").build()))
                                 .build();
    final Contact bob = Contact.builder()
                               .id(WID.<Contact>generate())
                               .handles(ImmutableSet.of(
                                   NameHandle.builder().validFrom(LocalDateTime.parse("1970-01-01")).name("Bob").build()))
                               .build();

    final Relationship aliceToBob = Relationship.builder()
                                                .from(alice.getId())
                                                .to(bob.getId())
                                                .uses(ImmutableSet.of(NameUse.builder()
                                                                             .name("Mr. Jones")
                                                                             .familiarity(50)
                                                                             .formality(50)
                                                                             .context(Context.PROFESSIONAL)
                                                                             .build(),
                                                                      NameUse.builder()
                                                                             .name("Bob Jones")
                                                                             .familiarity(20)
                                                                             .formality(50)
                                                                             .context(Context.PROFESSIONAL)
                                                                             .build(),
                                                                      NameUse.builder()
                                                                             .name("Dad")
                                                                             .familiarity(100)
                                                                             .formality(0)
                                                                             .context(Context.FAMILIAL)
                                                                             .build()))
        .build();
  }
}
