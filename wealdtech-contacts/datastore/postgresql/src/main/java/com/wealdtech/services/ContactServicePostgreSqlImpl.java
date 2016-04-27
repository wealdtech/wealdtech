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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.handles.Handle;
import com.wealdtech.contacts.services.ContactService;
import com.wealdtech.repository.ContactRepositoryPostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 *
 */
public class ContactServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Contact> implements ContactService<PreparedStatement>
{
  private static final Logger LOG = LoggerFactory.getLogger(ContactServicePostgreSqlImpl.class);

  private static final TypeReference<Contact> CONTACT_TYPE_REFERENCE = new TypeReference<Contact>() {};

  @Inject
  public ContactServicePostgreSqlImpl(final ContactRepositoryPostgreSqlImpl repository,
                                      @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "contact");
  }

  @Override
  public void create(final Contact contact)
  {
    super.add(contact);
  }

  @Override
  public Contact obtain(final WID<User> ownerId, final WID<Contact> contactId)
  {
    return Iterables.getFirst(obtain(CONTACT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
        setJson(stmt, index++, "{\"_id\":\"" + contactId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableList<Contact> obtain(final WID<User> ownerId)
  {
    return obtain(CONTACT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
      }
    });
  }

  @Override
  public ImmutableList<Contact> obtain(final WID<User> ownerId, final Handle handle)
  {
    return obtain(CONTACT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d @> ? AND d @> ?";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setJson(stmt, index++, "{\"ownerid\":\"" + ownerId.toString() + "\"}");
        setJson(stmt, index++, "{\"handles\":[{\"_key\":\"" + handle.getKey() + "\"}]}");
      }
    });
  }

  @Override
  public void remove(final Contact contact)
  {
    super.remove(contact.getId());
  }
}
