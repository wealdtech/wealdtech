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

import com.google.api.client.util.Sets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.wealdtech.ServerError;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.Credentials;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.ContactsClient;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Relationship;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.contacts.services.RelationshipService;
import com.wealdtech.contacts.uses.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A contacts sync service for Google Contacts
 */
public class ContactsSyncServiceSynchronousImpl<C extends Credentials> implements ContactsSyncService<C>
{
  private static final Logger LOG = LoggerFactory.getLogger(ContactsSyncServiceSynchronousImpl.class);

  // Access to our internal contacts service
  private ContactService<?> contactService;
  // Access to our internal relationship service
  private RelationshipService<?> relationshipService;

  // Access to the external contacts server
  private ContactsClient<C> contactsClient;

  public ContactsSyncServiceSynchronousImpl(final ContactService<?> contactService,
                                            final RelationshipService<?> relationshipService,
                                            final ContactsClient<C> contactsClient)
  {
    this.contactService = contactService;
    this.relationshipService = relationshipService;
    this.contactsClient = contactsClient;
  }

  @Override
  public int importContacts(final WID<User> userId, final C credentials, final boolean removeMissing)
  {
    Map<String, Contact> dbContacts = Maps.newHashMap();
    for (final Contact dbContact : contactService.obtain(userId))
    {
      final String remoteId = dbContact.obtainRemoteId(contactsClient.getRemoteService());
      if (remoteId != null)
      {
        dbContacts.put(remoteId, dbContact);
      }
    }

    final ImmutableList<Contact> contacts = contactsClient.obtainContacts(userId, credentials);
    for (Contact contact : contacts)
    {
      // Add/update the contact
      final Contact dbContact = matchContact(dbContacts, contact);
      if (dbContact == null)
      {
        if (contact.getId() == null)
        {
          contact = Contact.builder(contact).id(WID.<Contact>generate()).build();
        }
        contactService.create(contact);
      }
      else
      {
        // TODO this is incorrect; it overwrites information in the contact without merging it
        // (Or is this okay because we handle it in the universal merge system?)
        contact = Contact.builder(contact).id(dbContact.getId()).build();
        contactService.update(contact);
      }

      // Add/update the relationship(s)
      final Relationship dbRelationship;
      if (dbContact == null)
      {
        dbRelationship = null;

      }
      else
      {
        dbRelationship = relationshipService.obtainForContact(userId, dbContact.getId());
      }

      final ImmutableMap<String, ? extends Use> dbUses;
      if (dbRelationship == null)
      {
        dbUses = ImmutableMap.<String, Use>of();
      }
      else
      {
        dbUses = Maps.uniqueIndex(dbRelationship.getUses(), new Function<Use, String>()
        {
          @Nullable
          @Override
          public String apply(@Nullable final Use input)
          {
            return input == null ? null : input.getKey();
          }
        });
      }

      final Set<Use> newUses = Sets.newHashSet();
      for (final Handle handle : contact.getHandles())
      {
        if (handle.hasUse())
        {
          // We add a subset of uses depending on the sphere of the handle
          if (handle.getSpheres().isEmpty() || handle.getSpheres().contains(Handle.Sphere.PROFESSIONAL))
          {
            final String useKey = (Context.PROFESSIONAL.toString() + handle.getKey()).toLowerCase(Locale.ENGLISH);
            if (!dbUses.containsKey(useKey))
            {
              // Need to add it
              newUses.add(handle.toUse(Context.PROFESSIONAL, 1, 50));
            }
          }
          if (handle.getSpheres().isEmpty() || handle.getSpheres().contains(Handle.Sphere.PERSONAL))
          {
            final String socialUseKey = (Context.SOCIAL.toString() + handle.getKey()).toLowerCase(Locale.ENGLISH);
            if (!dbUses.containsKey(socialUseKey))
            {
              // Need to add it
              newUses.add(handle.toUse(Context.SOCIAL, 1, 50));
            }
            final String familialUseKey = (Context.FAMILIAL.toString() + handle.getKey()).toLowerCase(Locale.ENGLISH);
            if (!dbUses.containsKey(familialUseKey))
            {
              // Need to add it
              newUses.add(handle.toUse(Context.FAMILIAL, 1, 50));
            }
          }
        }
      }

      if (dbRelationship == null)
      {
        // Need to create a new relationship
        final Relationship relationship = Relationship.builder()
                                                      .id(WID.<Relationship>generate())
                                                      .ownerId(userId)
                                                      .to(contact.getId())
                                                      .uses(ImmutableSet.copyOf(newUses))
                                                      .build();
        relationshipService.create(relationship);
      }
      else
      {
        // Might need to update the existing relationship
        final Relationship relationship = Relationship.builder(dbRelationship)
                                                      .uses(ImmutableSet.<Use>builder().addAll(dbRelationship.getUses())
                                                                                       .addAll(newUses)
                                                                                       .build())
                                                      .build();
        relationshipService.update(relationship);
      }
    }

    return contacts.size();
  }

  @Nullable
  private Contact matchContact(final Map<String, Contact> dbContacts, final Contact contact)
  {
    final String remoteId = contact.obtainRemoteId(contactsClient.getRemoteService());
    if (remoteId != null && dbContacts.containsKey(remoteId))
    {
      return dbContacts.get(remoteId);
    }

    return null;
  }

  @Override
  public int exportContacts(final C credentials, final boolean removeUnknown)
  {
    // TODO implement
    throw new ServerError("Not implemented");
  }
}
