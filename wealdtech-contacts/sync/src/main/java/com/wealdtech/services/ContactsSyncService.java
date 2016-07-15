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

import com.wealdtech.User;
import com.wealdtech.WID;
import com.wealdtech.authentication.Credentials;

/**
 * Service to sync contact information
 */
public interface ContactsSyncService<C extends Credentials>
{
  /**
   * Import contacts from the given source
   *
   * @param userId the ID of the user importing the contents
   * @param credentials the credentials to access the sync source
   * @param removeMissing if {@code true} then remove any contacts in the sink that are not in the source
   * @return the number of contacts imported
   */
  int importContacts(final WID<User> userId, C credentials, boolean removeMissing);

  /**
   * Export contacts to the given source
   * @param credentials the credentials to access the export sink
   * @param removeUnknown carry out a full export by removing any items in the destination that are not in the source
   * @return the number of contacts exported
   */
  int exportContacts(C credentials, boolean removeUnknown);
}
