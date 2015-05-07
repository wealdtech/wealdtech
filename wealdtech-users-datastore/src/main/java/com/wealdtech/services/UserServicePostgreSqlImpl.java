/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.*;
import com.wealdtech.authentication.*;
import com.wealdtech.repositories.UserRepositoryPostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.util.Locale;

import static com.wealdtech.Preconditions.checkState;

/**
 * Access to users
 */
public class UserServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<User> implements UserService
{
  private static final Logger LOG = LoggerFactory.getLogger(UserServicePostgreSqlImpl.class);

  public static final ImmutableSet<String> RESERVED_NAMES = ImmutableSet.of("superuser", "system");

  private static final TypeReference<User> USER_TYPE_REFERENCE = new TypeReference<User>() {};

  @Inject
  public UserServicePostgreSqlImpl(final UserRepositoryPostgreSqlImpl repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "user");
  }

  @Override
  public void create(final User user)
  {
    // Ensure that none of the email addresses for the user are already in use
    for (final Email email : user.getEmails())
    {
      final User dupUser = obtain(email.getAddress());
      if (dupUser != null)
      {
        throw new DataError.Bad("The email address \"" + email.getAddress() + "\" is already assigned to another user");
      }
    }

    // Ensure that the user's name does not match any of the patterns for reserved names
    for (final String reservedName : RESERVED_NAMES)
    {
      if (user.getName().toLowerCase(Locale.ENGLISH).contains(reservedName))
      {
        throw new DataError.Bad("The user's name is not allowed to contain the value \"" + reservedName + "\"");
      }
    }

    add(user);

    rationaliseIdentities(null, user);
  }

  @Override
  public User obtain(final Credentials credentials)
  {
    checkState(credentials != null, "Cannot obtain user without credentials");

    User user = null;
    User dbUser = null;
    switch (credentials.getType())
    {
      case PasswordCredentials.PASSWORD_CREDENTIALS:
        // For password-based credentials we obtain the user via the name, which is the user's email address
        final PasswordCredentials passwordCredentials = (PasswordCredentials)credentials;
        dbUser = obtain(passwordCredentials.getName());
        if (dbUser != null)
        {
          final AuthorisationScope scope = new PasswordAuthenticator().authenticate(credentials, dbUser.getAuthenticationMethods());
          if (scope != AuthorisationScope.NONE)
          {
            // User is authenticated
            user = dbUser;
          }
        }
        break;
      case TokenCredentials.TOKEN_CREDENTIALS:
        // For token-based credentials we obtain the user via the token
        final TokenCredentials tokenCredentials = (TokenCredentials)credentials;
        dbUser = Iterables.getFirst(obtain(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
        {
          @Override
          public String getConditions()
          {
            return "d->'authenticationmethods' @> '[{\"type\":\"" + TokenAuthenticationMethod.TOKEN_AUTHENTICATION +
                   "\"}]' AND d->'authenticationmethods' @> ?";
          }

          @Override
          public void setConditionValues(final PreparedStatement stmt)
          {
            int index = 1;
            setJson(stmt, index++, "[{\"token\":\"" + tokenCredentials.getToken() + "\"}]");
          }
        }), null);
        if (dbUser != null)
        {
          // We have obtained the user via the single-use token, so remove the token
          final User.Builder<?> updatedUserB = User.builder(dbUser);

          final ImmutableSet.Builder<AuthenticationMethod> authenticationMethodsB = ImmutableSet.builder();
          boolean found = false;
          for (final AuthenticationMethod authenticationMethod : dbUser.getAuthenticationMethods())
          {
            if (Objects.equal(authenticationMethod.getType(), TokenAuthenticationMethod.TOKEN_AUTHENTICATION) &&
                Objects.equal(((TokenAuthenticationMethod)authenticationMethod).getToken(), tokenCredentials.getToken()))
            {
              found = true;
            }
            else
            {
              authenticationMethodsB.add(authenticationMethod);
            }
          }
          if (!found)
          {
            // Didn't find the token, be upset
            throw new DataError.Bad("Failed to find token to remove on use");
          }
          updatedUserB.authenticationMethods(authenticationMethodsB.build());

          user = updatedUserB.build();
          update(dbUser, user);
        }
        break;
      //      {
      //        // For all other credentials we obtain the user via the auth ID, which is part of the authentication method
      //        return Iterables.getFirst(obtain(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
      //        {
      //          @Override
      //          public String getConditions()
      //          {
      //            return "d->'authenticationmethods' @> ? AND d->'authenticationmethods' @> ?";
      //          }
      //
      //          @Override
      //          public void setConditionValues(final PreparedStatement stmt)
      //          {
      //            int index = 1;
      //            setJson(stmt, index++, "[{\"type\":\"" + credentials.getAuthType().orNull() + "\"}]");
      //            setJson(stmt, index++, "[{\"authid\":\"" + credentials.getAuthId().orNull() + "\"}]");
      //          }
      //        }), null);
      //      }
    }
    return user;

  }


  @Override
  public User obtain(final WID<User> id)
  {
    return Iterables.getFirst(obtain(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
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
        setJson(stmt, index++, "{\"_id\":\"" + id.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public ImmutableSet<User> obtain(final ImmutableCollection<WID<User>> ids)
  {
    return ImmutableSet.copyOf(obtain(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getConditions()
      {
        return "d ->>'_id' = ANY(?)";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setWIDArray(stmt, index++, ids);
      }
    }));
  }

  @Override
  public User obtain(final String emailAddress)
  {
    // PERF this is very inefficient as it pulls the entire user table.  Fix the CTE?
    return Iterables.getFirst(query(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getQuery()
      {
        return "WITH emails AS (SELECT d, (jsonb_array_elements(d->'emails')->>'address')::CITEXT AS email FROM t_user)\n" +
               "SELECT d FROM emails WHERE email = ?::CITEXT";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setString(stmt, index++, emailAddress);
      }
    }), null);
  }

  @Override
  public ImmutableSet<User> obtainAll()
  {
    return ImmutableSet.copyOf(obtain(USER_TYPE_REFERENCE, null));
  }

  @Override
  public void remove(final User user)
  {
    // Fetch the current user from the database
    final User dbUser = obtain(user.getId());

    // Ensure that we are deleting what we think we are deleting
    if (!Objects.equal(User.serialize(user), User.serialize(dbUser)))
    {
      throw new ServerError("Must provide matching user for deletion");
    }

    // Remove it
    remove(user.getId());

    rationaliseIdentities(user, null);
  }

  @Override
  public void update(final User oldUser, final User newUser)
  {
    checkState(oldUser != null, "Missing old user");
    checkState(newUser != null, "Missing new user");
    checkState(Objects.equal(oldUser.getId(), newUser.getId()), "Attempt to update user with different IDs");

    for (final Email email : newUser.getEmails())
    {
      final User dupUser = obtain(email.getAddress());
      if (dupUser != null && !Objects.equal(dupUser.getId(), newUser.getId()))
      {
        throw new DataError.Bad("The email address \"" + email.getAddress() + "\" is already in use");
      }
    }

    // Ensure that the user's name does not match any of the patterns for reserved names
    for (final String reservedName : RESERVED_NAMES)
    {
      if (newUser.getName().toLowerCase(Locale.ENGLISH).contains(reservedName))
      {
        throw new DataError.Bad("The user's name is not allowed to contain the value \"" + reservedName + "\"");
      }
    }

    update(newUser);
    rationaliseIdentities(oldUser, newUser);
  }

  @Override
  public void verifyEmail(final Credentials credentials)
  {
    throw new ServerError("Not implemented");
    //    checkState(credentials != null, "Credentials required for email verification");
    //    checkState(credentials.getAuthType().orNull() == AuthenticationMethodType.TOKEN, "Token required for email verification");
    //    checkState(credentials.getSecret().isPresent(), "Token required for email verification");
    //
    //    // Find the user with the verification token so that we can obtain the email address it is verifying
    //    // Note that we can't just obtain() the user because that consumes the token and won't have the email address
    //    final User tokenUser = Iterables.getFirst(obtain(USER_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    //    {
    //      @Override
    //      public String getConditions()
    //      {
    //        return "d->'authenticationmethods' @> '[{\"type\":\"" + AuthenticationMethodType.TOKEN.toString() +
    //               "\"}]' AND d->'authenticationmethods' @> ?";
    //      }
    //
    //      @Override
    //      public void setConditionValues(final PreparedStatement stmt)
    //      {
    //        int index = 1;
    //        setJson(stmt, index++, "[{\"secret\":\"" + credentials.getSecret().orNull() + "\"}]");
    //      }
    //    }), null);
    //    if (tokenUser == null)
    //    {
    //      throw new DataError.Bad("Failed to verify email address");
    //    }
    //    String address = null;
    //    for (final AuthenticationMethod authenticationMethod : tokenUser.getAuthenticationMethods())
    //    {
    //      if (authenticationMethod.getType() == AuthenticationMethodType.TOKEN &&
    //          Objects.equal(authenticationMethod.getSecret().orNull(), credentials.getSecret().get()))
    //      {
    //        address = authenticationMethod.getAuthId();
    //        break;
    //      }
    //    }
    //    if (address == null)
    //    {
    //      throw new DataError.Bad("Failed to verify email address");
    //    }
    //
    //    // Now obtain the user - this consumes the verification token
    //    final User dbUser = obtain(credentials);
    //    if (dbUser == null)
    //    {
    //      throw new DataError.Bad("Failed to verify email address");
    //    }
    //
    //    // Update the user to set the email address to verified and to remove the verification token
    //    final User.Builder<?> updatedUserB = User.builder(dbUser);
    //
    //    // Update the relevant email address
    //    final ImmutableSet.Builder<Email> emailsB = ImmutableSet.builder();
    //    for (final Email email : dbUser.getEmails())
    //    {
    //      if (email.getAddress().equalsIgnoreCase(address))
    //      {
    //        // Create a new verified copy
    //        emailsB.add(Email.builder().address(email.getAddress()).primary(email.isPrimary()).verified(true).build());
    //      }
    //      else
    //      {
    //        emailsB.add(email);
    //      }
    //    }
    //    updatedUserB.emails(emailsB.build());
    //
    //
    //    update(dbUser, updatedUserB.build());
  }

  @JsonIgnore
  private static final TypeReference<GenericWObject> WOBJECT_TYPE_REFERENCE = new TypeReference<GenericWObject>() {};

  @Override
  public ImmutableSet<GenericWObject> obtainKnownEmails(final ImmutableCollection<String> emailAddresses)
  {
    // The case of the emails that we store might be different to that of the ones provided.  We need to return emails in the case
    // in which they were provided rather than that in the datastore, so that the calling user can easily carry out direct string
    // comparisons

    final ImmutableSet.Builder<GenericWObject> resultsB = ImmutableSet.builder();

    // PERF this is O(n^2); various better ways to do this
    for (final GenericWObject address : query(WOBJECT_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
    {
      @Override
      public String getQuery()
      {
        return "WITH emails AS (SELECT d->>'_id' AS id, jsonb_array_elements(d->'emails')->>'address' AS email FROM t_user)\n" +
               "SELECT row_to_json(t) FROM (SELECT emails.id, emails.email FROM emails WHERE email::CITEXT = ANY(?)) t";
      }

      @Override
      public void setConditionValues(final PreparedStatement stmt)
      {
        int index = 1;
        setCIStringArray(stmt, index++, emailAddresses);
      }
    }))
    {
      for (final String emailAddress : emailAddresses)
      {
        if (emailAddress.equalsIgnoreCase(address.get("email", String.class).get()))
        {
          resultsB.add(GenericWObject.builder()
                                     .data("id", address.get("id", String.class).orNull())
                                     .data("email", emailAddress)
                                     .build());
          break;
        }
      }
    }

    return resultsB.build();
  }

  /**
   * Ensure that whenever a user is created, updated or deleted other users' identity information is consistent
   */
  private void rationaliseIdentities(@Nullable final User oldUser, @Nullable final User newUser)
  {
    checkState(oldUser != null || newUser != null, "rationalise cannot work with NULL old and new users");

    // Create sets of newly-created and now-removed identity authentication methods
    final Iterable<AuthenticationMethod> newIdentities = Iterables.filter(Sets.difference(newUser == null ?
                                                                                          ImmutableSet.<AuthenticationMethod>of() :
                                                                                          newUser.getAuthenticationMethods(),
                                                                                          oldUser == null ?
                                                                                          ImmutableSet.<AuthenticationMethod>of() :
                                                                                          oldUser.getAuthenticationMethods()),
                                                                          new Predicate<AuthenticationMethod>()
                                                                          {
                                                                            @Override
                                                                            public boolean apply(@Nullable final AuthenticationMethod input)
                                                                            {
                                                                              return input != null && Objects.equal(input.getType(),
                                                                                                                    IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION);
                                                                            }
                                                                          });

    final Iterable<AuthenticationMethod> oldIdentities = Iterables.filter(Sets.difference(oldUser == null ?
                                                                                          ImmutableSet.<AuthenticationMethod>of() :
                                                                                          oldUser.getAuthenticationMethods(),
                                                                                          newUser == null ?
                                                                                          ImmutableSet.<AuthenticationMethod>of() :
                                                                                          newUser.getAuthenticationMethods()),
                                                                          new Predicate<AuthenticationMethod>()
                                                                          {
                                                                            @Override
                                                                            public boolean apply(@Nullable final AuthenticationMethod input)
                                                                            {
                                                                              return input != null && Objects.equal(input.getType(),
                                                                                                                    IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION);
                                                                            }
                                                                          });

    for (final AuthenticationMethod oldIdentity : oldIdentities)
    {
      // This user is no longer allowing someone else to take their identity.  Remove access from their user
      final User otherUser = obtain(((IdentityAuthenticationMethod)oldIdentity).getUserId());
      if (otherUser != null)
      {
        final ImmutableSet.Builder<Identity> identitiesB = ImmutableSet.builder();
        for (final Identity identity : otherUser.getIdentities())
        {
          if (!Objects.equal(WID.<User>recast(identity.getIdentityId()), oldUser.getId()))
          {
            identitiesB.add(identity);
          }
        }
        update(otherUser, User.builder(otherUser).identities(identitiesB.build()).build());
      }
    }

    for (final AuthenticationMethod newIdentity : newIdentities)
    {
      // This user is allowing someone else to take their identity.  Add access to their user
      final User otherUser = obtain(((IdentityAuthenticationMethod)newIdentity).getUserId());
      if (otherUser != null)
      {
        final ImmutableSet.Builder<Identity> identitiesB = ImmutableSet.builder();
        for (final Identity identity : otherUser.getIdentities())
        {
          if (!Objects.equal(WID.<User>recast(identity.getIdentityId()), newUser.getId()))
          {
            identitiesB.add(identity);
          }
        }
        identitiesB.add(Identity.builder().identityId(WID.<Identity>recast(newUser.getId())).scope(newIdentity.getScope()).build());
        update(otherUser, User.builder(otherUser).identities(identitiesB.build()).build());
      }
    }
  }
}
