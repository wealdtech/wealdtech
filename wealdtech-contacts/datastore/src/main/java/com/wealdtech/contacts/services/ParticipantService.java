/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.services;

import com.google.common.collect.ImmutableSet;
import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.contacts.Contact;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.Participant;

import javax.annotation.Nullable;

/**
 *
 */
public interface ParticipantService
{
  /**
   * Obtain participants given a context and some way of identifying them
   *
   * @param ownerId the ID of the user obtaining the participant
   * @param context the context of the relationship
   * @param contactId the ID of the contact for whom to obtain the participants
   * @param name the name of the contact for whom to obtain the participants
   * @param email the email address of the contact for whom to obtain the participants
   * @return all participants matching the provided information
   */
  ImmutableSet<Participant> obtain(WID<User> ownerId,
                                   Context context,
                                   @Nullable WID<Contact> contactId,
                                   @Nullable String name,
                                   @Nullable String email);
}
