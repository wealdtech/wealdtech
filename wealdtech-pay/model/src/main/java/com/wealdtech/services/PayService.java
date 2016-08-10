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
 * The pay service handles payments
 */
public interface PayService
{

  /**
   * Escrow funds for a transaction.
   * Placing funds in to escrow means that they are held by a third party and unable to be accessed by either the originator
   * or recipient until released.
   * @param transaction the transaction to escrow
   */
  void escrowFunds(Transaction transaction);

  /**
   * Release funds held in escrow.
   * Releasing funds in escrow means that they are sent on to the intended recipient.
   * @param transactionId the ID of the transaction for whose funds will be released
   */
  void releaseFunds(WID<Transaction> transactionId);

  /**
   * Return funds held in escrow.
   * Returning funds in escrow means that they are sent back to the originator.
   * @param transactionId the ID of the transaction for whose funds will be returned
   */
  void returnFunds(WID<Transaction> transactionId);

  /**
   * Send funds.
   * Sending funds moves them directly from the originator to the recipient
   */
  void sendFunds(Transaction transaction);
}
