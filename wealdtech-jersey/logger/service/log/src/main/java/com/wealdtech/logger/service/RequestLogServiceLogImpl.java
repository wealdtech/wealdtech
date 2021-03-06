/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.logger.service;

import com.wealdtech.logger.RequestLogEntry;
import com.wealdtech.logger.services.RequestLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class RequestLogServiceLogImpl implements RequestLogService
{
  private static final Logger LOG = LoggerFactory.getLogger(RequestLogServiceLogImpl.class);

  @Override
  public void create(final RequestLogEntry requestLogEntry)
  {
    LOG.info("{}", RequestLogEntry.serialize(requestLogEntry));
  }
}
