/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.notifications.providers;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wealdtech.GenericWObject;
import com.wealdtech.WObject;
import com.wealdtech.services.gcm.GcmClient;

/**
 */
public class NotificationProviderGcmImpl implements NotificationProvider
{
  private GcmClient client;

  @Inject
  public NotificationProviderGcmImpl(final GcmClient client)
  {
    this.client = client;
  }


  @Override
  public void notify(final ImmutableSet<String> recipients, final WObject<?> msg)
  {
    // Wrap the object in a 'data' tag
    final GenericWObject dataMsg = GenericWObject.builder().data("data", msg.getAllData()).build();
    client.sendMessage(recipients, dataMsg);
  }
}
