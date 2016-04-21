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
import com.google.common.collect.ImmutableSet;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.ServiceException;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;
import com.wealdtech.authentication.OAuth2Credentials;
import com.wealdtech.contacts.config.ContactsConfiguration;
import com.wealdtech.contacts.events.BirthEvent;
import com.wealdtech.contacts.events.Event;
import com.wealdtech.contacts.events.WeddingEvent;
import com.wealdtech.contacts.handles.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * Implementation of the contacts client using the Google Contacts API
 */
public class ContactsClientGoogleContactsImpl implements ContactsClient<OAuth2Credentials>
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
    return obtainContactsByEmail(credentials, "default", null);
  }

  @Override
  public ImmutableList<Contact> obtainContacts(final OAuth2Credentials credentials, final String query)
  {
    return obtainContactsByEmail(credentials, "default", query);
  }

  @Override
  public ImmutableList<Contact> obtainContactsByEmail(final OAuth2Credentials credentials, final String email, final String query)
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
      if (query != null)
      {
        myQuery.setFullTextQuery(query);
      }
      myQuery.setMaxResults(99999);
      ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
      for (ContactEntry entry : resultFeed.getEntries())
      {
        resultsB.add(googleContactToContact(entry));
      }
    }
    catch (final IOException | ServiceException ioe)
    {
      ioe.printStackTrace();
    }

    return resultsB.build();
  }

  private Contact googleContactToContact(final ContactEntry googleContact)
  {
    final Contact.Builder<?> builder = Contact.builder();

    if (googleContact.hasImAddresses()) { System.err.println(googleContact.getImAddresses()); }
//    System.err.println(googleContact.getName().getFullName());
//    System.err.println(googleContact.getContactPhotoLink().getHref());

    builder.remoteIds(ImmutableSet.of(googleContact.getId() + "@google"));

    final ImmutableSet.Builder<Handle> handlesB = ImmutableSet.builder();
    boolean addedHandle = false;

    if (googleContact.hasName())
    {
      final Name name = googleContact.getName();
      if (name.hasFullName())
      {
        handlesB.add(NameHandle.builder().name(name.getFullName().getValue()).build());
        addedHandle = true;
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
          handlesB.add(NameHandle.builder().name(sb.toString()).build());
          addedHandle = true;
        }
      }
    }
    else
    {
      LOG.warn("Contact " + googleContact.getId() + " has no name");
    }

    if (googleContact.hasNickname())
    {
      handlesB.add(NickNameHandle.builder().name(googleContact.getNickname().getValue()).build());
      addedHandle = true;
    }

    for (final Email email : googleContact.getEmailAddresses())
    {
      final EmailHandle.Builder<?> handleB = EmailHandle.builder();
      if (email.getAddress() != null)
      {
        handleB.address(email.getAddress());
      }
      if (email.getDisplayName() != null)
      {
        handleB.displayName(email.getDisplayName());
      }
      if (Objects.equals(email.getRel(), Email.Rel.HOME))
      {
        handleB.spheres(ImmutableSet.of(Handle.Sphere.PERSONAL));
      }
      else if (Objects.equals(email.getRel(), Email.Rel.WORK))
      {
        handleB.spheres(ImmutableSet.of(Handle.Sphere.PROFESSIONAL));
      }

      handlesB.add(handleB.build());
      addedHandle = true;
    }

    for (final PhoneNumber number : googleContact.getPhoneNumbers())
    {
      final TelephoneHandle.Builder<?> handleB = TelephoneHandle.builder();
      if (number.getPhoneNumber() != null)
      {
        handleB.number(number.getPhoneNumber());
      }
      if (Objects.equals(number.getRel(), PhoneNumber.Rel.HOME))
      {
        handleB.spheres(ImmutableSet.of(Handle.Sphere.PERSONAL));
      }
      else if (Objects.equals(number.getRel(), PhoneNumber.Rel.WORK))
      {
        handleB.spheres(ImmutableSet.of(Handle.Sphere.PROFESSIONAL));
      }
      // FIXME add type of number (landline, mobile, etc.)

      handlesB.add(handleB.build());
      addedHandle = true;
    }

    for (final Website website : googleContact.getWebsites())
    {
      // Work on the HREF rather than the label, as label isn't present for some lesser-known sites
      if (AboutMeHandle.matchesAccountUrl(website.getHref()))
      {
        final AboutMeHandle.Builder<?> handleB =
            AboutMeHandle.builder().localId(AboutMeHandle.localIdFromAccountUrl(website.getHref()));
        if (website.getRel() == Website.Rel.HOME)
        {
          handleB.spheres(ImmutableSet.of(Handle.Sphere.PERSONAL));
        }
        else if (website.getRel() == Website.Rel.WORK)
        {
          handleB.spheres(ImmutableSet.of(Handle.Sphere.PROFESSIONAL));
        }
        handlesB.add(AboutMeHandle.builder().localId(AboutMeHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (DisqusHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(DisqusHandle.builder().localId(DisqusHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (FacebookHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(FacebookHandle.builder().localId(FacebookHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (FlickrHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(FlickrHandle.builder().localId(FlickrHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (FourSquareHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(FourSquareHandle.builder().localId(FourSquareHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (GoogleHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(GoogleHandle.builder().localId(GoogleHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (GravatarHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(GravatarHandle.builder().localId(GravatarHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (KloutHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(KloutHandle.builder().localId(KloutHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (LinkedInHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(LinkedInHandle.builder().localId(LinkedInHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (TripItHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(TripItHandle.builder().localId(TripItHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (TwitterHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(TwitterHandle.builder().localId(TwitterHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (VimeoHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(VimeoHandle.builder().localId(VimeoHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else if (YouTubeHandle.matchesAccountUrl(website.getHref()))
      {
        handlesB.add(YouTubeHandle.builder().localId(YouTubeHandle.localIdFromAccountUrl(website.getHref())).build());
        addedHandle = true;
      }
      else
      {
        // Generic website
        handlesB.add(WebsiteHandle.builder().url(website.getHref()).build());
      }
    }

    if (addedHandle)
    {
      builder.handles(handlesB.build());
    }

    final ImmutableSet.Builder<Event> eventsB = ImmutableSet.builder();
    boolean addedEvent = false;

    if (googleContact.getBirthday() != null)
    {
      final LocalDate birthday = LocalDate.parse(googleContact.getBirthday().getWhen());
      eventsB.add(BirthEvent.builder().date(birthday).build());
      addedEvent = true;
    }

    for (final com.google.gdata.data.contacts.Event event : googleContact.getEvents())
    {
      System.err.println(event.getLabel());
      switch (event.getLabel())
      {
        case "Anniversary":
          final LocalDate date = LocalDate.parse(event.getWhen().getValueString());
          eventsB.add(WeddingEvent.builder().date(date).build());
          addedEvent = true;
          break;
        default:
          LOG.info("Unknown event label " + event.getLabel());

      }
    }

    if (addedEvent)
    {
      builder.events(eventsB.build());
    }

    for (final PostalAddress postalAddress : googleContact.getPostalAddresses())
    {
      System.err.println(postalAddress);
      // TODO or use getStructuredPostalAddresses?
    }

    for (final ExtendedProperty extendedProperty : googleContact.getExtendedProperties())
    {
      // TODO
//      System.err.println(extendedProperty.getName() + ": " + extendedProperty.getValue());
    }

    for (final UserDefinedField udf : googleContact.getUserDefinedFields())
    {
      System.err.println("udf: " + udf);
    }

    for (final GroupMembershipInfo info : googleContact.getGroupMembershipInfos())
    {
      System.err.println("info: " + info);
    }

    if (googleContact.hasGender())
    {
      final Gender gender = googleContact.getGender();
      System.err.println("gender: " + gender);
    }

    for (final Hobby hobby : googleContact.getHobbies())
    {
      System.err.println("hobby: " + hobby);
    }

    for (final Jot jot : googleContact.getJots())
    {
      System.err.println("jot: " + jot);
    }

    if (googleContact.hasOccupation())
    {
      final Occupation occupation = googleContact.getOccupation();
      System.err.println("occupation: " + occupation);
    }

    for (final Relation relation: googleContact.getRelations())
    {
      System.err.println("relation: " + relation);
    }

    System.err.println("updated: " + googleContact.getUpdated());

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
