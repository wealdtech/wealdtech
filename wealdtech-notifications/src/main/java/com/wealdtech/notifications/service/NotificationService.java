/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.notifications.service;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.ServerError;
import com.wealdtech.WObject;
import com.wealdtech.notifications.config.NotificationConfiguration;
import com.wealdtech.notifications.providers.NotificationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class NotificationService
{
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  private final NotificationConfiguration configuration;
  private final NotificationProvider provider;

  @Inject
  public NotificationService(final NotificationConfiguration configuration)
  {
    this.configuration = configuration;

    try
    {
      Class<NotificationProvider> clazz = (Class<NotificationProvider>)Class.forName(configuration.getProvider());
      Constructor<NotificationProvider> constructor = clazz.getConstructor();
      this.provider = constructor.newInstance();
    }
    catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e)
    {
      LOG.error("Failed to find notification service class {}: ", configuration.getProvider(), e);
      throw new ServerError("Failed to find notification service class " + configuration.getProvider(), e);
    }
  }

  public void notify(ImmutableSet<String> recipients, WObject<?> msg)
  {
    provider.notify(recipients, msg);
  }
}
