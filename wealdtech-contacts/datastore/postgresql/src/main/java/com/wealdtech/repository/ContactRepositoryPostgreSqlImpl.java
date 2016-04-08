/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.contacts.repository.ContactRepository;
import com.wealdtech.datastore.config.PostgreSqlConfiguration;
import com.wealdtech.repositories.PostgreSqlRepository;

/**
 *
 */
public class ContactRepositoryPostgreSqlImpl extends PostgreSqlRepository implements ContactRepository
{
  @Inject
  public ContactRepositoryPostgreSqlImpl(@Named("contactrepositoryconfiguration") final PostgreSqlConfiguration configuration)
  {
    super(configuration);
  }
}
