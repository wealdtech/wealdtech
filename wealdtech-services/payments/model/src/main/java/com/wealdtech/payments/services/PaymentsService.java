/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.payments.services;

import com.wealdtech.Money;
import com.wealdtech.payments.PaymentRecipient;
import com.wealdtech.payments.PaymentReport;

/**
 * A service that provides payment facilities
 */
public interface PaymentsService
{
  PaymentReport pay(Money value, PaymentRecipient recipient);
}
