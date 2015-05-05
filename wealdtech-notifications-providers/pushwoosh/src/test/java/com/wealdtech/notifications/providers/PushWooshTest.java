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
import com.wealdtech.GenericWObject;
import com.wealdtech.notifications.service.NotificationService;
import com.wealdtech.services.config.PushWooshConfiguration;
import com.wealdtech.services.pushwoosh.PushWooshClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 */
public class PushWooshTest
{
  NotificationService notificationService;

  @BeforeClass
  public void setUp()
  {
    notificationService =
        new NotificationService(new NotificationProviderPushWooshImpl(new PushWooshClient(new PushWooshConfiguration("appId",
                                                                                                                     "apiKey"))));
  }

  @AfterClass
  public void tearDown(){}

  @Test
  public void testSendMessage()
  {
    notificationService.notify(ImmutableSet.of("APA91bGcxjphD0JKlayw5Yxe4vdUzgjVZW6SmVqSJ5ifiWPixeysbFEDPRb_EtO40Tf2Y4JLtxpOY0_QvDz4n2GW0WLQiuNpOX5LnbgvPEUaNIRX46HG8dNO1RWCQuJsGfrHFL6t08XZafxLVNbLsjTcE3AdFrbbMA"),
                               GenericWObject.builder().data("message", "hello world").build());
  }
}
