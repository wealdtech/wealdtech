/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.mail.MailActor;
import com.wealdtech.mail.MandrillClient;
import com.wealdtech.mail.MandrillMessage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import test.com.wealdtech.mail.config.ApplicationModule;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class MandrillServiceTest
{
  private MandrillClient client;

  @BeforeClass
  public void setUp()
  {
    final Injector injector = Guice.createInjector(new ApplicationModule("config-mandrill.json"));
    client = injector.getInstance(MandrillClient.class);
  }

  @Test(groups = {"base"})
  public void testPing()
  {
    assertTrue(client.ping());
  }
}
