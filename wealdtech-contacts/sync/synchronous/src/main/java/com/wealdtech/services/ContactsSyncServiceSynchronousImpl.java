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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.wealdtech.ServerError;
import com.wealdtech.WID;
import com.wealdtech.authentication.Credentials;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.ContactsClient;
import com.wealdtech.contacts.services.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * A contacts sync service for Google Contacts
 */
public class ContactsSyncServiceSynchronousImpl<C extends Credentials> implements ContactsSyncService<C>
{
  private static final Logger LOG = LoggerFactory.getLogger(ContactsSyncServiceSynchronousImpl.class);

  // Access to our internal contacts service
  private ContactService contactService;
  // Access to the external contacts service
  private ContactsClient<C> contactsClient;

  public ContactsSyncServiceSynchronousImpl(final ContactService contactService, final ContactsClient<C> contactsClient)
  {
    this.contactService = contactService;
    this.contactsClient = contactsClient;
  }

  @Override
  public int importContacts(final C credentials, final boolean removeMissing)
  {
    Map<String, Contact> dbContacts = Maps.newHashMap();
    for (final Contact dbContact : contactService.obtain())
    {
      final String remoteId = dbContact.obtainRemoteId(contactsClient.getRemoteService());
      if (remoteId != null)
      {
        dbContacts.put(remoteId, dbContact);
      }
    }

    final ImmutableList<Contact> contacts = contactsClient.obtainContacts(credentials);
    for (Contact contact : contacts)
    {
      final Contact dbContact = matchContact(dbContacts, contact);
      if (dbContact != null)
      {
        contact = Contact.builder(contact).id(dbContact.getId()).build();
        contactService.update(contact);
      }
      else
      {
        if (contact.getId() == null)
        {
          contact = Contact.builder(contact).id(WID.<Contact>generate()).build();
        }
        contactService.create(contact);
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
