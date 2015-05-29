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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wealdtech.Application;
import com.wealdtech.WID;
import com.wealdtech.datastore.repositories.ApplicationRepositoryPostgreSqlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 * Application service using PostgreSQL as a backend
 */
public class ApplicationServicePostgreSqlImpl extends WObjectServicePostgreSqlImpl<Application> implements ApplicationService
{
  private static final Logger LOG = LoggerFactory.getLogger(ApplicationServicePostgreSqlImpl.class);

  private static final TypeReference<Application> APPLICATION_TYPE_REFERENCE = new TypeReference<Application>() {};

  @Inject
  public ApplicationServicePostgreSqlImpl(final ApplicationRepositoryPostgreSqlImpl repository, @Named("dbmapper") final ObjectMapper mapper)
  {
    super(repository, mapper, "application");
  }

  @Override
  public void create(final Application application)
  {
    super.add(application);
  }

  @Override
  public Application obtain(final WID<Application> appId)
  {
    return Iterables.getFirst(obtain(APPLICATION_TYPE_REFERENCE, new WObjectServiceCallbackPostgreSqlImpl()
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
        setJson(stmt, index++, "{\"_id\":\"" + appId.toString() + "\"}");
      }
    }), null);
  }

  @Override
  public void remove(final Application application)
  {
    super.remove(application.getId());
  }
}
