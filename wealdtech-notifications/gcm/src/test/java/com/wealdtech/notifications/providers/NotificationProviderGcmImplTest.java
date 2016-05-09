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
import com.wealdtech.notifications.Notification;
import com.wealdtech.notifications.service.NotificationService;
import com.wealdtech.services.config.GcmConfiguration;
import com.wealdtech.services.gcm.GcmClient;
import org.testng.annotations.Test;

/**
 */
public class NotificationProviderGcmImplTest
{
  @Test
  public void testSimple()
  {
    final GcmClient client = new GcmClient(GcmConfiguration.fromEnv("wealdtech_gcm_test"));
    final NotificationService service = new NotificationService(new NotificationProviderGcmImpl(client));
    service.notify(ImmutableSet.of("test1", "test2"), Notification.builder().data("key1", "val1").data("key2", "val2").build());
  }
}
