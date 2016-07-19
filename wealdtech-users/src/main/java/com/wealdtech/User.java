/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech; // NOPMD

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.wealdtech.authentication.AuthenticationMethod;
import com.wealdtech.authentication.Credentials;
import com.wealdtech.authentication.IdentityAuthenticationMethod;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * User contains all information related directly to a user
 */
public class User extends WObject<User> implements Comparable<User>
{
  private static final Logger LOG = LoggerFactory.getLogger(User.class);

  private static final String NAME = "name";
  private static final String EMAILS = "emails";
  private static final String AUTHENTICATION_METHODS = "authenticationmethods";
  private static final String DEVICE_REGISTRATIONS = "deviceregistrations";
  private static final String IDENTITIES = "identities";
  private static final String CREDENTIALS = "credentials";
  private static final String AVATAR = "avatar";
  private static final String FRIEND_IDS = "friendids";
  private static final String BLOCKED_IDS = "blockedids";

  @JsonCreator
  public User(final Map<String, Object> data)
  {
    super(data);
  }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    if (!data.containsKey(VERSION))
    {
      data.put(VERSION, 0);
    }
    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    checkState(exists(ID), "User failed validation: must contain ID");
    checkState(exists(VERSION), "User failed validation: must contain version");
    checkState(exists(NAME), "User failed validation: must contain name");
    checkState(!findState().equals(UserState.INVALID), "User failed validation: invalid state");

    boolean hasPrimaryEmailAddr = false;
    for (final Email email : this.getEmails())
    {
      if (email.isPrimary())
      {
        if (hasPrimaryEmailAddr)
        {
          LOG.debug("User {} failed validation: multiple primary email addresses", this.getId());
          throw new DataError.Bad("User contains multiple primary email addresses");
        }
        hasPrimaryEmailAddr = true;
      }
    }
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<User> getId(){ return super.getId(); }

  @JsonIgnore
  public LocalDateTime getCreated() { return get(CREATED, LocalDateTime.class).get(); }

  @JsonIgnore
  public LocalDateTime getModified() { return get(MODIFIED, LocalDateTime.class).get(); }

  @JsonIgnore
  public Integer getVersion() { return get(VERSION, Integer.class).get(); }


  @JsonIgnore
  public String getName(){ return get(NAME, String.class).get(); }

  @JsonIgnore
  private static final TypeReference<ImmutableSet<Email>> EMAILS_TYPEREF = new TypeReference<ImmutableSet<Email>>() {};

  @JsonIgnore
  public ImmutableSet<Email> getEmails(){ return get(EMAILS, EMAILS_TYPEREF).or(ImmutableSet.<Email>of()); }

  @JsonIgnore
  private static final TypeReference<ImmutableSet<AuthenticationMethod>> AUTHENTICATION_METHODS_TYPEREF =
      new TypeReference<ImmutableSet<AuthenticationMethod>>() {};

  @JsonIgnore
  public ImmutableSet<AuthenticationMethod> getAuthenticationMethods()
  {
    return get(AUTHENTICATION_METHODS, AUTHENTICATION_METHODS_TYPEREF).or(ImmutableSet.<AuthenticationMethod>of());
  }

  @JsonIgnore
  private static final TypeReference<ImmutableSet<DeviceRegistration>> DEVICE_REGISTRATIONS_TYPEREF =
      new TypeReference<ImmutableSet<DeviceRegistration>>() {};

  @JsonIgnore
  public ImmutableSet<DeviceRegistration> getDeviceRegistrations()
  {
    return get(DEVICE_REGISTRATIONS, DEVICE_REGISTRATIONS_TYPEREF).or(ImmutableSet.<DeviceRegistration>of());
  }

  private static final TypeReference<ImmutableSet<Identity>> IDENTITIES_TYPEREF = new TypeReference<ImmutableSet<Identity>>() {};
  @JsonIgnore
  public ImmutableSet<Identity> getIdentities(){ return get(IDENTITIES, IDENTITIES_TYPEREF).or(ImmutableSet.<Identity>of()); }

  private static final TypeReference<ImmutableSet<? extends Credentials>> CREDENTIALS_TYPEREF =
      new TypeReference<ImmutableSet<? extends Credentials>>() {};

  @JsonIgnore
  public ImmutableSet<? extends Credentials> getCredentials()
  {
    return get(CREDENTIALS, CREDENTIALS_TYPEREF).or(ImmutableSet.<Credentials>of());
  }

  @JsonIgnore
  public Optional<String> getAvatar(){ return get(AVATAR, String.class); }

  private static final TypeReference<ImmutableSet<WID<User>>> FRIEND_IDS_TYPEREF = new TypeReference<ImmutableSet<WID<User>>>() {};
  @JsonIgnore
  public ImmutableSet<WID<User>> getFriendIds(){ return get(FRIEND_IDS, FRIEND_IDS_TYPEREF).or(ImmutableSet.<WID<User>>of()); }

  private static final TypeReference<ImmutableSet<WID<User>>> BLOCKED_IDS_TYPEREF = new TypeReference<ImmutableSet<WID<User>>>() {};
  @JsonIgnore
  public ImmutableSet<WID<User>> getBlockedIds(){ return get(BLOCKED_IDS, BLOCKED_IDS_TYPEREF).or(ImmutableSet.<WID<User>>of()); }

  @JsonIgnore
  @Nullable
  public Email findEmail(final String address)
  {
    for (final Email email : getEmails())
    {
      if (email.getAddress().equalsIgnoreCase(address))
      {
        return email;
      }
    }
    return null;
  }

  /**
   * @return the primary email for the user; can be {@code null} if this user is an identity
   */
  @JsonIgnore
  @Nullable
  public Email findPrimaryEmail()
  {
    for (final Email email : getEmails())
    {
      if (email.isPrimary())
      {
        return email;
      }
    }
    return null;
  }

  /**
   * @return the IDs of this user's identities
   */
  @JsonIgnore
  public ImmutableSet<WID<Identity>> findIdentityIds()
  {
    return ImmutableSet.copyOf(Iterables.transform(this.getIdentities(), new Function<Identity, WID<Identity>>()
                                                   {
                                                     @Nullable
                                                     @Override
                                                     public WID<Identity> apply(@Nullable final Identity input)
                                                     {
                                                       return input == null ? null : input.getIdentityId();
                                                     }
                                                   }));
  }

  /**
   * Obtain the state of the user.  The state of the user will be one of the following: <ul> <li>VERIFIED</li> <li>UNVERIFIED</li>
   * <li>IDENTITY</li> <li>INACTIVE</li> <li>UNKNOWN</li> </ul> The specifics of how each state is determined is as follows: <table
   * border="1"> <tr align="center"><td>&nbsp;</td><td colspan=2>Authentication</td><td colspan=2>Email</td></tr> <tr
   * align="center"><td>State</td><td>Non-identity</td><td>Identity</td><td>Unverified</td><td>Verified</td></tr> <tr
   * align="center"><td>Inactive</td><td>0</td><td>0</td><td>1..*</td><td>0</td></tr> <tr align="center"><td>Identity</td><td>0</td><td>1..*</td><td>0</td><td>0</td></tr>
   * <tr align="center"><td>Unverified</td><td>1..*</td><td>0..*</td><td>1..*</td><td>0</td></tr> <tr
   * align="center"><td>Verified</td><td>1..*</td><td>0..*</td><td>0..*</td><td>1..*</td></tr> </table> If the state is outside of
   * the above it is labelled as unknown.
   *
   * @return The state of the user
   */
  @JsonIgnore
  public UserState findState()
  {
    if (isVerified())
    {
      return UserState.VERIFIED; // NOPMD
    }

    if (isUnverified())
    {
      return UserState.UNVERIFIED; // NOPMD
    }

    if (isIdentity())
    {
      return UserState.IDENTITY; // NOPMD
    }

    if (isInactive())
    {
      return UserState.INACTIVE; // NOPMD
    }

    // Shouldn't reach here
    LOG.warn("Invalid state for user {}", this);

    return UserState.INVALID;
  }

  // User predicates
  @JsonIgnore
  public boolean isVerified()
  {
    final Predicate<User> predicate =
        Predicates.and(HasNonIdentityAuthenticationsPredicate.p(Range.atLeast(1)), HasVerifiedEmailsPredicate.p(Range.atLeast(1)));
    return predicate.apply(this);
  }

  @JsonIgnore
  public boolean isUnverified()
  {
    final Predicate<User> predicate = Predicates.and(HasNonIdentityAuthenticationsPredicate.p(Range.atLeast(1)),
                                                     HasUnverifiedEmailsPredicate.p(Range.atLeast(1)));
    return predicate.apply(this);
  }

  @JsonIgnore
  public boolean isIdentity()
  {
    final Predicate<User> predicate = Predicates.and(Arrays.asList(HasNonIdentityAuthenticationsPredicate.p(Range.singleton(0)),
                                                                   HasIdentityAuthenticationsPredicate.p(Range.atLeast(1)),
                                                                   HasUnverifiedEmailsPredicate.p(Range.singleton(0)),
                                                                   HasVerifiedEmailsPredicate.p(Range.singleton(0))));
    return predicate.apply(this);
  }

  @JsonIgnore
  public boolean isInactive()
  {
    final Predicate<User> predicate = Predicates.and(Arrays.asList(HasNonIdentityAuthenticationsPredicate.p(Range.singleton(0)),
                                                                   HasIdentityAuthenticationsPredicate.p(Range.singleton(0)),
                                                                   HasUnverifiedEmailsPredicate.p(Range.atLeast(1)),
                                                                   HasVerifiedEmailsPredicate.p(Range.singleton(0))));
    return predicate.apply(this);
  }

  @JsonIgnore
  public boolean canRepresent(final WID<User> userId)
  {
    return CanRepresentPredicate.p(this).apply(userId);
  }

  @JsonIgnore
  public boolean canBeRepresentedBy(final WID<User> userId)
  {
    return CanBeRepresentedByPredicate.p(this).apply(userId);
  }

  static class HasIdentityAuthenticationsPredicate implements Predicate<User>
  {
    private final Range<Integer> range;

    private HasIdentityAuthenticationsPredicate(final Range<Integer> range)
    {
      this.range = range;
    }

    @Override
    public boolean apply(final User user)
    {
      int identityAuthenticationMethods = 0;
      for (final AuthenticationMethod authenticationMethod : user.getAuthenticationMethods())
      {
        if (Objects.equal(IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION, authenticationMethod.getType()))
        {
          identityAuthenticationMethods++;
        }
      }
      return range.contains(identityAuthenticationMethods);
    }

    public static Predicate<User> p(final Range<Integer> range)
    {
      return new HasIdentityAuthenticationsPredicate(range);
    }
  }

  static class HasNonIdentityAuthenticationsPredicate implements Predicate<User>
  {
    private final Range<Integer> range;

    private HasNonIdentityAuthenticationsPredicate(final Range<Integer> range)
    {
      this.range = range;
    }

    @Override
    public boolean apply(final User user)


    {
      int nonIdentityAuthenticationMethods = 0;
      for (final AuthenticationMethod authenticationMethod : user.getAuthenticationMethods())
      {
        if (!Objects.equal(IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION, authenticationMethod.getType()))
        {
          nonIdentityAuthenticationMethods++;
        }
      }
      return range.contains(nonIdentityAuthenticationMethods);
    }

    public static Predicate<User> p(final Range<Integer> range)
    {
      return new HasNonIdentityAuthenticationsPredicate(range);
    }
  }

  static class HasUnverifiedEmailsPredicate implements Predicate<User>
  {
    private final Range<Integer> range;

    private HasUnverifiedEmailsPredicate(final Range<Integer> range)
    {
      this.range = range;
    }

    @Override
    public boolean apply(final User user)
    {
      int unverifiedemails = 0;
      for (final Email email : user.getEmails())
      {
        if (!email.isVerified())
        {
          unverifiedemails++;
        }
      }
      return range.contains(unverifiedemails);
    }

    public static Predicate<User> p(final Range<Integer> range)
    {
      return new HasUnverifiedEmailsPredicate(range);
    }
  }

  static class HasVerifiedEmailsPredicate implements Predicate<User>
  {
    private final Range<Integer> range;

    private HasVerifiedEmailsPredicate(final Range<Integer> range)
    {
      this.range = range;
    }

    @Override
    public boolean apply(final User user)
    {
      int verifiedemails = 0;
      for (final Email email : user.getEmails())
      {
        if (email.isVerified())
        {
          verifiedemails++;
        }


      }
      return range.contains(verifiedemails);
    }

    public static Predicate<User> p(final Range<Integer> range)
    {
      return new HasVerifiedEmailsPredicate(range);
    }
  }

  // Predicates to handle user/user interactions
  static class CanRepresentPredicate implements Predicate<WID<User>>
  {
    private final User user;

    private CanRepresentPredicate(final User user)
    {
      this.user = user;
    }

    @Override
    public boolean apply(final WID<User> identityId)
    {
      // Check for simple case of identity == user
      if (Objects.equal(this.user.getId(), identityId))
      {
        return true; // NOPMD
      }
      // Check if this user has access to the identity
      for (final Identity allowedIdentity : user.getIdentities())
      {
        if (Objects.equal(WID.<User>recast(allowedIdentity.getIdentityId()), identityId))
        {
          return true;
        }
      }
      return false;
    }

    public static Predicate<WID<User>> p(final User user)
    {
      return new CanRepresentPredicate(user);
    }

  }

  static class CanBeRepresentedByPredicate implements Predicate<WID<User>>
  {
    private final User user;

    private CanBeRepresentedByPredicate(final User user)
    {
      this.user = user;
    }

    @Override
    public boolean apply(final WID<User> identityId)
    {
      boolean result = false;
      // Check for simple case of identity == user
      if (Objects.equal(this.user.getId(), identityId))
      {
        result = true;
      }
      if (!result)
      {
        // Check if this user has an identity authentication for the identity
        for (final AuthenticationMethod authenticationMethod : this.user.getAuthenticationMethods())
        {
          if (Objects.equal(IdentityAuthenticationMethod.IDENTITY_AUTHENTICATION, authenticationMethod.getType()) &&
              Objects.equal(((IdentityAuthenticationMethod)authenticationMethod).getUserId(), identityId))
          {
            result = true;
          }
        }
      }
      return result;
    }

    public static Predicate<WID<User>> p(final User user)
    {
      return new CanBeRepresentedByPredicate(user);
    }

  }

  /**
   * Merge this user with additional data supplied in an overlay and return a new user representing the merged user.
   *
   * @param overlay A user with some fields potentially null.
   *
   * @return A new user containing merged information
   * @throws DataError If there is a problem with the data
   */
  public User mergeWith(final User overlay)
  {
    final Builder<?> builder = User.builder(this);
    if (overlay.getName() != null)
    {
      builder.name(overlay.getName());
    }
    if (overlay.getEmails() != null)
    {
      builder.emails(overlay.getEmails());
    }
    if (overlay.getAuthenticationMethods() != null)
    {
      builder.authenticationMethods(overlay.getAuthenticationMethods());
    }
    return builder.build();
  }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<User, P>
  {
    public Builder(){ super(); }

    public Builder(final User prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P emails(final ImmutableSet<Email> emails)
    {
      data(EMAILS, emails);
      return self();
    }

    public P authenticationMethods(final ImmutableSet<? extends AuthenticationMethod> authenticationMethods)
    {
      data(AUTHENTICATION_METHODS, authenticationMethods);
      return self();
    }

    public P deviceRegistrations(final ImmutableSet<DeviceRegistration> deviceRegistrations)
    {
      data(DEVICE_REGISTRATIONS, deviceRegistrations);
      return self();
    }

    public P identities(final ImmutableSet<Identity> identities)
    {
      data(IDENTITIES, identities);
      return self();
    }

    public P credentials(final ImmutableSet<? extends Credentials> credentials)
    {
      data(CREDENTIALS, credentials);
      return self();
    }

    public P avatar(final String avatar)
    {
      data(AVATAR, avatar);
      return self();
    }

    public P friendIds(final ImmutableSet<WID<User>> friendIds)
    {
      data(FRIEND_IDS, friendIds);
      return self();
    }

    public P blockedIds(final ImmutableSet<WID<User>> blockedIds)
    {
      data(BLOCKED_IDS, blockedIds);
      return self();
    }

    public User build(){ return new User(data); }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final User prior)
  {
    return new Builder(prior);
  }
}
