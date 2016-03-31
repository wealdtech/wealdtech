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

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.ImmutableList;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.UserDefinedField;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.contacts.config.ContactsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * Implementation of the contacts client using the Google Contacts API
 */
public class ContactsClientGoogleContactsImpl implements ContactsClient
{
  private static final Logger LOG = LoggerFactory.getLogger(ContactsClientGoogleContactsImpl.class);

  private final ContactsConfiguration configuration;

  private final HttpTransport httpTransport;

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private final ContactsService contactsService;

  public ContactsClientGoogleContactsImpl(final ContactsConfiguration configuration) throws GeneralSecurityException, IOException
  {
    this.configuration = configuration;
    this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();

    this.contactsService = new ContactsService(configuration.getProductId());
  }


  @Nullable
  @Override
  public Contact obtainContact(final OAuth2Credentials credentials, final String contactId)
  {
    return obtainContact(credentials, "default", contactId);
  }

  @Nullable
  @Override
  public Contact obtainContact(final OAuth2Credentials credentials, final String email, final String contactId)
  {
    throw new ServerError("Not implemented");
  }

  @Override
  public ImmutableList<Contact> obtainContacts(final OAuth2Credentials credentials)
  {
    return obtainContacts(credentials, "default");
  }

  @Override
  public ImmutableList<Contact> obtainContacts(final OAuth2Credentials credentials, final String email)
  {
    final URL feedUrl;
    try
    {
      feedUrl = new URL("https://www.google.com/m8/feeds/contacts/" + email + "/full");
    }
    catch (final MalformedURLException mue)
    {
      throw new DataError.Bad("Bad URL when obtaining contacts");
    }

    final ImmutableList.Builder<Contact> resultsB = ImmutableList.builder();
    try
    {
      contactsService.setOAuth2Credentials(generateCredential(credentials));
      final Query myQuery = new Query(feedUrl);
      myQuery.setMaxResults(99999);
      ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
      System.err.println("Number of entries is " + resultFeed.getEntries().size());
      for (ContactEntry entry : resultFeed.getEntries())
      {
        resultsB.add(googleContactToContact(entry));
      }
    }
    catch (final IOException ioe)
    {
      ioe.printStackTrace();
    }
    catch (final ServiceException se)
    {
      se.printStackTrace();
    }

    return resultsB.build();
  }

  private Contact googleContactToContact(final ContactEntry googleContact)
  {
    googleContact.addUserDefinedField(new UserDefinedField("My test field", "My test value"));
    final Contact.Builder<?> builder = Contact.builder();
    if (googleContact.hasName())
    {
      final Name name = googleContact.getName();
      if (name.hasFullName())
      {
        builder.name(name.getFullName().getValue());
      }
      else
      {
        final StringBuilder sb = new StringBuilder();
        if (name.hasGivenName())
        {
          sb.append(name.getGivenName());
          sb.append(' ');
        }
        if (name.hasFamilyName())
        {
          sb.append(name.getFamilyName());
        }
        if (sb.length() > 0)
        {
          builder.name(sb.toString());
        }
      }
    }
    return builder.build();
  }

  private Credential generateCredential(final OAuth2Credentials credentials)
  {
    return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(httpTransport)
                                                                                .setJsonFactory(JSON_FACTORY)
                                                                                .setClientAuthentication(new OAuthParameters())
                                                                                .setTokenServerUrl(new GenericUrl("https://accounts.google.com/o/oauth2/token"))
                                                                                .build()
                                                                                .setAccessToken(credentials.getAccessToken())
                                                                                .setRefreshToken(credentials.getRefreshToken());
  }
}
