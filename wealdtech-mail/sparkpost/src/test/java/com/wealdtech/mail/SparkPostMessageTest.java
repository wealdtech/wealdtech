/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.mail.config.ApplicationModule;
import com.wealdtech.mail.services.MailService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 */
public class SparkPostMessageTest
{
  private MailService service;

  @BeforeClass
  public void setUp()
  {
    final Injector injector = Guice.createInjector(new ApplicationModule("config-sparkpost.json"));
    service = injector.getInstance(MailServiceSparkPostImpl.class);
  }

  @Test(groups = {"base"})
  public void testSimpleMessage() throws JsonProcessingException
  {
    final ImmutableList<MailActor> recipients = ImmutableList.of(new MailActor("WealdTech", "test@wealdtech.com"));
    final MailResponse response = service.sendEmail(recipients, "Test", "This is a test", "<h1>Test</h1>This is a test.");
    assertTrue(response.getStatus().contains("\"total_accepted_recipients\": 1"));
  }

  @Test(groups = {"base"})
  public void testTemplateMessage() throws JsonProcessingException
  {
//    final ImmutableList<MailActor> recipients = ImmutableList.of(new MailActor("WealdTech", "test@wealdtech.com"));
//    final ImmutableList<ImmutableMap<String, String>> merge = ImmutableList.of(ImmutableMap.of("REPLACEME", "WealdTech"));
//    final MailResponse response = service.sendTemplate("test", "Test Email", merge, recipients);
//    assertTrue(response.getStatus().contains("\"total_accepted_recipients\": 1"));
  }
}
