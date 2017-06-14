/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.wealdtech.config.MangoPayConfiguration;
import com.wealdtech.mangopay.CardRegistration;
import com.wealdtech.services.MangoPayClient;
import org.joda.time.LocalDate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test MangoPay
 */
public class MangoPayClientTest
{
  private MangoPayClient client;

  @BeforeClass
  public void setUp()
  {
    final MangoPayConfiguration configuration = MangoPayConfiguration.fromEnv("wealdtech_mangopay_test");
    client = new MangoPayClient(configuration);
  }

  @Test
  public void testPing()
  {
    assertTrue(client.ping());
  }

  @Test
  public void testCreateUser()
  {
    String id = client.createUser("Test", "User", "Test@test.com", new LocalDate(1970, 1, 1), "GB", "GB", "test");
    assertNotNull(id);
  }

  @Test
  public void testCreateCardRegistration()
  {
    String id = client.createUser("Test", "User", "Test@test.com", new LocalDate(1970, 1, 1), "GB", "GB", "test");
    assertNotNull(id);

    final CardRegistration registration =
        client.createCardRegistration(id, CreditCard.Brand.VISA, Currency.getInstance("GBP"), "Test");

    assertNotNull(registration);
  }

  @Test
  public void testCreateCardPayin()
  {
    client.payIn("15275627", "15275645", "15274507", "15274508",
                 Money.builder().amount(new BigDecimal("10.00")).currency("GBP").build(),
                 Money.builder().amount(new BigDecimal("1.00")).currency("GBP").build());
  }
}
