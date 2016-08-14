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

import org.joda.time.YearMonth;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 *
 */
public class CreditCardTest
{
  @Test
  public void testObtainBrand1()
  {
    // Simple test for VISA
    assertEquals(CreditCard.Brand.fromCardNumber("4500"), CreditCard.Brand.VISA);
  }

  @Test
  public void testObtainBrand2()
  {
    // Simple test for no brand
    assertNull(CreditCard.Brand.fromCardNumber("3070 0000 0000 0000"));
  }

  @Test
  public void testObtainBrand3()
  {
    // Simple test for value in the middle of another brand's information
    assertEquals(CreditCard.Brand.fromCardNumber("6221 2500 0000 0000"), CreditCard.Brand.CHINA_UNIONPAY);
    assertEquals(CreditCard.Brand.fromCardNumber("6221 2600 0000 0000"), CreditCard.Brand.DISCOVER);
    assertEquals(CreditCard.Brand.fromCardNumber("6229 2500 0000 0000"), CreditCard.Brand.DISCOVER);
    assertEquals(CreditCard.Brand.fromCardNumber("6229 2600 0000 0000"), CreditCard.Brand.CHINA_UNIONPAY);
  }

  @Test
  public void testDeserCard()
  {
    // Check deserialization of a card
    final String value = "{\"number\":\"4111 1111 11111 111\",\"expiry\":\"09/19\",\"csc\":\"123\"}";
    final CreditCard card = CreditCard.deserialize(value, CreditCard.class);
    assertNotNull(card);
    assertEquals(card.getNumber(), "4111111111111111");
    assertEquals(card.getExpiry(), new YearMonth(2019, 9));
    assertEquals(card.getCsc(), "123");
    assertEquals(CreditCard.Brand.fromCardNumber(card.getNumber()), CreditCard.Brand.VISA);
  }
}
