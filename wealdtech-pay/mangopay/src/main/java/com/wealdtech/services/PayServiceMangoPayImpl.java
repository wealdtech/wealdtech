/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.wealdtech.Transaction;
import com.wealdtech.WID;

/**
 * An implementation of Wealdtech's Pay service using MangoPay as the backend
 */
public class PayServiceMangoPayImpl implements PayService
{
  @Override
  public void escrowFunds(final Transaction transaction)
  {

  }

  @Override
  public void releaseFunds(final WID<Transaction> transactionId)
  {

  }

  @Override
  public void returnFunds(final WID<Transaction> transactionId)
  {

  }

  @Override
  public void sendFunds(final Transaction transaction)
  {

  }
}
